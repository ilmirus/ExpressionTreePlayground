package _in

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        fun foo(x: Int, y: Int, c: Collection<Int>) {
            x in c && y !in c
        }
    }
    return "OK"
}