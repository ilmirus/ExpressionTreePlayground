package gl

import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressiontree.debugExpressionTree

fun expressionTree(block: GL_Context.() -> Unit): FirExpression = TODO()

val globals = expressionTree {
    @uniform lateinit var uProjectionMatrix: mat4

    @uniform lateinit var uModelMatrix: mat4

    @uniform lateinit var uTargetFraction: float

    @uniform lateinit var uPrevFraction: float

    @uniform lateinit var uExpand: float

    @uniform lateinit var uCullMode: float

    @uniform lateinit var uCameraPosition: vec3

    @uniform lateinit var uNormalMatrix: mat3

    @attribute lateinit var aVertexPosition: vec3

    @attribute lateinit var aPrevVertexPosition: vec3

    @attribute lateinit var aVertexNormal: vec3

    @attribute lateinit var aPrevVertexNormal: vec3

    @varying lateinit var vColorMul: float

    fun fInterpolatedPosition(): vec3 {
        return aVertexPosition * uTargetFraction + aPrevVertexPosition * uPrevFraction
    }

    fun fInterpolatedNormal(): vec3 {
        return aVertexNormal * uTargetFraction + aPrevVertexNormal * uPrevFraction
    }

    fun fPosition(): vec4 {
        return uModelMatrix * vec4(fInterpolatedPosition() + fInterpolatedNormal() * uExpand, 1.0)
    }

    fun fFaceDirection(position: vec4, normal: vec3): float {
        return dot(position.xyz - uCameraPosition, normal)
    }

    fun fNormal(): vec3 {
        return uNormalMatrix * fInterpolatedNormal()
    }
}

fun main() {
    val shader = expressionTree {
        val position = fPosition()
        gl_Position = uProjectionMatrix * position
        vColorMul =
            if (uCullMode == 0.0) 1.0
            else if (fFaceDirection(position, fNormal()) * uCullMode >= 0.0) 1.0
            else 0.0
    }

//    println("GLOBALS")
//    println(globals.debugExpressionTree())
//    println()
//    println("MAIN")
//    println(shader.debugExpressionTree())

    val transformer = FirToGLTransformer()
    transformer.transformGlobalDeclarations(globals as FirBlock)
    transformer.transformMainDeclaration(shader as FirBlock)

    println(transformer.buffer.toString())

    smoke.main()
}