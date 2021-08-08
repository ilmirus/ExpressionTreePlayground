package labelForInfix

fun box(): String {
    val tree = expressionTree {
        val length = 1
        fun bar() {}
        infix fun (() -> Int).foo(c: () -> Unit) {}
        { length } foo { bar() }
    }
    return "OK"
}

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}
