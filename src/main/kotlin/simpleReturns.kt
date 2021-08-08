package simpleReturns

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        fun foo() {
            return
        }

        fun bar(): String {
            return "Hello"
        }
    }
    return "OK"
}