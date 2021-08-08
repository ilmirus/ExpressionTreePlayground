package nullability

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        fun orFourtyTwo(arg: Int?) = arg ?: 42

        fun bang(arg: Int?) = arg!!
    }
    return "OK"
}