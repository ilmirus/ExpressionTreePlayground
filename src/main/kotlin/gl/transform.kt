package gl

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.expressions.impl.FirElseIfTrueCondition
import org.jetbrains.kotlin.fir.expressiontree.debugExpressionTree
import org.jetbrains.kotlin.fir.references.impl.*
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.types.*

class FirToGLTransformer {
    private val properties = arrayListOf<FirProperty>()
    private val functions = arrayListOf<FirFunction>()

    private var indent = 0

    private val builtinTypes = setOf(
        "float",
        "vec", "vec2", "vec3", "vec4",
        "mat", "mat2", "mat3", "mat4",
        "int",
        "ivec", "ivec2", "ivec3", "ivec4",
        "bool",
        "bvec", "bvec2", "bvec3", "bvec4",
        "void"
    )

    private val annotationNames = setOf(
        "uniform", "attribute", "varying", "const"
    )

    val buffer = StringBuffer()

    private fun putIndent() {
        buffer.append((0..indent).joinToString("") { " " })
    }

    private fun declareGlobals(block: FirBlock) {
        for (stmt in block.statements) {
            when (stmt) {
                is FirProperty -> properties += stmt
                is FirSimpleFunction -> functions += stmt
                else -> error("Unexpected $stmt")
            }
        }
    }

    private fun typeName(type: FirTypeRef): String? = (type as? FirUserTypeRef)?.let {
        qualifierString(it.qualifier).removePrefix("\"").removeSuffix("\"")
    }

    private fun qualifierString(qualifiers: List<FirQualifierPart>): String =
        qualifiers.joinToString(".") {
            it.name.asString() + if (it.typeArgumentList.typeArguments.isEmpty()) ""
            else it.typeArgumentList.typeArguments.joinToString(prefix = "<", postfix = ">") { projection ->
                require(projection is FirTypeProjectionWithVariance)
                require(projection.typeRef is FirUserTypeRef)
                when (projection.variance) {
                    Variance.INVARIANT -> ""
                    Variance.IN_VARIANCE -> "in "
                    Variance.OUT_VARIANCE -> "out "
                } + typeName(projection.typeRef as FirUserTypeRef)
            }
        }

    private fun analyzeDeclarations() {
        for (function in functions) {
            checkAnnotations(function.annotations)
            require(function.returnTypeRef is FirUserTypeRef)
            require(typeName(function.returnTypeRef) in builtinTypes) {
                "${typeName(function.returnTypeRef)} is not builtin"
            }
            require(function is FirSimpleFunction)
            require(!function.name.isSpecial)
            require(!function.status.isInline)
            for (param in function.valueParameters) {
                require(param.annotations.isEmpty())
                require(!param.isVararg)
                require(param.typeParameters.isEmpty())
                require(param.defaultValue == null)
                require(param.returnTypeRef is FirUserTypeRef)
                require(typeName(param.returnTypeRef) in builtinTypes)
            }
            require(function.body != null)
        }
        for (property in properties) {
            checkAnnotations(property.annotations)
            require(property.returnTypeRef is FirUserTypeRef)
            require(typeName(property.returnTypeRef) in builtinTypes)
        }
    }

    private fun annotationName(annotation: FirAnnotationCall): String? =
        (annotation.calleeReference as? FirSimpleNamedReference)?.name?.asString()

    private fun checkAnnotations(annotations: List<FirAnnotationCall>) {
        require(annotations.size < 2)
        for (annotation in annotations) {
            require(annotationName(annotation) in annotationNames) {
                annotation.debugExpressionTree() + " should be uniform, varying, attribute or const"
            }
        }
    }

    fun transformGlobalDeclarations(block: FirBlock): String {
        declareGlobals(block)
        analyzeDeclarations()
        return dumpDeclarations()
    }

    fun transformMainDeclaration(block: FirBlock): String {
        val types = mutableMapOf<FirProperty, FirTypeRef>()

        for (statement in block.statements) {
            if (statement is FirProperty) {
                if (statement.returnTypeRef is FirImplicitTypeRef) {
                    types[statement] = inferType(statement.initializer!!)
                }
            }
        }

        buffer.append("int main() {\n")
        indent += 2
        for (statement in block.statements) {
            putIndent()
            dumpExpression(statement, types)
            buffer.append(";\n")
        }
        putIndent()
        buffer.append("return 0;\n}\n")
        indent -= 2
        return buffer.toString()
    }

    private fun inferType(element: FirElement): FirTypeRef {
        when (element) {
            is FirFunctionCall -> {
                val ref = element.calleeReference
                require(ref is FirSimpleNamedReference)
                return functions.find { it is FirSimpleFunction && it.name == ref.name }!!.returnTypeRef
            }
            else -> error("$element is unsupported: ${element.debugExpressionTree()}")
        }
    }

    private fun dumpDeclarations(): String {
        for (property in properties) {
            dumpGlobalVariableDeclaration(property)
            buffer.append(";\n")
        }

        for (function in functions) {
            dumpFunctionHeader(function)
            buffer.append(";\n")
        }

        for (function in functions) {
            dumpFunctionHeader(function)
            buffer.append(" {\n")
            indent += 2
            for (stmt in function.body!!.statements) {
                putIndent()
                dumpExpression(stmt, emptyMap())
                buffer.append(";\n")
            }
            indent -= 2
            buffer.append("}\n")
        }

        return buffer.toString()
    }

    private fun dumpExpression(expression: FirElement, types: Map<FirProperty, FirTypeRef>) {
        when (expression) {
            is FirReturnExpression -> {
                buffer.append("return ")
                dumpExpression(expression.result, types)
            }
            is FirFunctionCall -> {
                buffer.append("(")
                expression.explicitReceiver?.let { dumpExpression(it, types) }
                val name = (expression.calleeReference as FirSimpleNamedReference).name.asString()
                var operator = false
                when (name) {
                    "times" -> buffer.append(" * ")
                    "plus" -> buffer.append(" + ")
                    "minus" -> buffer.append(" - ")
                    else -> {
                        if (expression.explicitReceiver != null) {
                            buffer.append(".")
                        }
                        buffer.append(name)
                        operator = false
                    }
                }
                if (!operator) {
                    buffer.append("(")
                    if (expression.arguments.isNotEmpty()) {
                        for (arg in expression.arguments.dropLast(1)) {
                            dumpExpression(arg, types)
                            buffer.append(", ")
                        }
                        dumpExpression(expression.arguments.last(), types)
                    }
                    buffer.append(")")
                } else {
                    dumpExpression(expression.arguments.single(), types)
                }
                buffer.append(")")
            }
            is FirQualifiedAccessExpression -> {
                if (expression.explicitReceiver != null) {
                    dumpExpression(expression.explicitReceiver!!, types)
                    buffer.append(".")
                }
                dumpExpression(expression.calleeReference, types)
            }
            is FirSimpleNamedReference -> {
                buffer.append(expression.name.asString())
            }
            is FirConstExpression<*> -> {
                when (expression.kind) {
                    ConstantValueKind.Null -> buffer.append("NULL")
                    ConstantValueKind.Boolean -> buffer.append(expression.value)
                    ConstantValueKind.Char -> buffer.append(expression.value)
                    ConstantValueKind.Byte -> buffer.append(expression.value)
                    ConstantValueKind.UnsignedByte -> buffer.append(expression.value)
                    ConstantValueKind.Short -> buffer.append(expression.value)
                    ConstantValueKind.UnsignedShort -> buffer.append(expression.value)
                    ConstantValueKind.Int -> buffer.append(expression.value)
                    ConstantValueKind.UnsignedInt -> buffer.append(expression.value)
                    ConstantValueKind.Long -> buffer.append(expression.value)
                    ConstantValueKind.UnsignedLong -> buffer.append(expression.value)
                    ConstantValueKind.String -> buffer.append(expression.value)
                    ConstantValueKind.Float -> buffer.append(expression.value)
                    ConstantValueKind.Double -> buffer.append(expression.value)
                    ConstantValueKind.IntegerLiteral -> buffer.append(expression.value)
                    ConstantValueKind.UnsignedIntegerLiteral -> buffer.append(expression.value)
                }
            }
            is FirProperty -> {
                for (annotation in expression.annotations) {
                    dumpAnnotation(annotation)
                }
                buffer.append(typeName(expression.returnTypeRef) ?: typeName(types[expression]!!))
                buffer.append(" ")
                buffer.append(expression.name.asString())
                buffer.append(" = ")
                dumpExpression(expression.initializer!!, types)
            }
            is FirVariableAssignment -> {
                buffer.append((expression.calleeReference as FirSimpleNamedReference).name.asString())
                buffer.append(" = ")
                dumpExpression(expression.rValue, types)
            }
            is FirWhenExpression -> {
                buffer.append("(")
                require(expression.branches.size == 2)
                val condition = expression.branches.find { it.condition !is FirElseIfTrueCondition }!!
                val default = expression.branches.find { it.condition is FirElseIfTrueCondition }!!
                dumpExpression(condition.condition, types)
                buffer.append(" ? ")
                require(condition.result.statements.size == 1)
                dumpExpression(condition.result.statements.first(), types)
                buffer.append(" : ")
                require(default.result.statements.size == 1)
                dumpExpression(default.result.statements.first(), types)
                buffer.append(")")
            }
            is FirEqualityOperatorCall -> {
                require(expression.arguments.size == 2)
                buffer.append("(")
                dumpExpression(expression.arguments[0], types)
                buffer.append(") ")
                dumpOperation(expression.operation)
                buffer.append(" (")
                dumpExpression(expression.arguments[1], types)
                buffer.append(")")
            }
            is FirComparisonExpression -> {
                buffer.append("(")
                dumpExpression(expression.compareToCall.explicitReceiver!!, types)
                buffer.append(") ")
                dumpOperation(expression.operation)
                buffer.append(" (")
                require(expression.compareToCall.arguments.isNotEmpty()) {
                    expression.compareToCall.debugExpressionTree()
                }
                dumpExpression(expression.compareToCall.argument, types)
                buffer.append(")")
            }
            else -> error("$expression is unsupported: ${expression.debugExpressionTree()}")
        }
    }

    private fun dumpOperation(operation: FirOperation) {
        when (operation) {
            FirOperation.EQ -> buffer.append("==")
            FirOperation.NOT_EQ -> buffer.append("!=")
            FirOperation.IDENTITY -> buffer.append("===")
            FirOperation.NOT_IDENTITY -> buffer.append("!==")
            FirOperation.LT -> buffer.append("<")
            FirOperation.GT -> buffer.append(">")
            FirOperation.LT_EQ -> buffer.append("<=")
            FirOperation.GT_EQ -> buffer.append(">=")
            FirOperation.ASSIGN -> buffer.append("=")
            FirOperation.PLUS_ASSIGN -> buffer.append("+=")
            FirOperation.MINUS_ASSIGN -> buffer.append("-=")
            FirOperation.TIMES_ASSIGN -> buffer.append("*=")
            FirOperation.DIV_ASSIGN -> buffer.append("/=")
            FirOperation.REM_ASSIGN -> buffer.append("%=")
            FirOperation.EXCL -> buffer.append("!")
            FirOperation.IS -> buffer.append("is")
            FirOperation.NOT_IS -> buffer.append("!is")
            FirOperation.AS -> buffer.append("as")
            FirOperation.SAFE_AS -> buffer.append("as?")
            else -> error("$operation is not supported")
        }
    }

    private fun dumpFunctionHeader(function: FirFunction) {
        function.annotations.firstOrNull()?.let { dumpAnnotation(it) }
        buffer.append(typeName(function.returnTypeRef))
        buffer.append(" ")
        buffer.append((function as FirSimpleFunction).name.asString())
        buffer.append(function.valueParameters.joinToString(prefix = "(", postfix = ")") {
            typeName(it.returnTypeRef) + " " + it.name.asString()
        })
    }

    private fun dumpGlobalVariableDeclaration(property: FirProperty) {
        property.annotations.firstOrNull()?.let { dumpAnnotation(it) }
        buffer.append(typeName(property.returnTypeRef))
        buffer.append(" ")
        buffer.append(property.name.asString())
        if (!property.status.isLateInit) {
            TODO("Add body for properties")
        }
    }

    private fun dumpAnnotation(it: FirAnnotationCall) {
        buffer.append(annotationName(it))
        buffer.append(" ")
    }
}

fun FirExpression.transformGlobalDeclarations(): String =
    FirToGLTransformer().transformGlobalDeclarations(this as FirBlock)

fun FirExpression.transformMainDeclaration(): String = FirToGLTransformer().transformMainDeclaration(this as FirBlock)
