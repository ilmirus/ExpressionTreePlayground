package variables

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        fun test(): Int {
            val x = 1
            var y = x + 1
            val z = y * 2
            y = y + z
            val w = y - x
            return w
        }
    }
    return "OK"
}