package inBrackets

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        fun test(e: Int.() -> String) {
            val s = 3.e()
            val ss = 3.(e)()
        }
    }
    return "OK"
}