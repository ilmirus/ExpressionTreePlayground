package init

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        class WithInit(x: Int) {
            val x: Int

            init {
                this.x = x
            }
        }
    }
    return "OK"
}
