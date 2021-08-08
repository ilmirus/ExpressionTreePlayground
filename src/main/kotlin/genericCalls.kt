package genericCalls

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        fun <T> nullableValue(): T? = null

        fun test() = expressionTree {
            val n = nullableValue<Int>()
            val x = nullableValue<Double>()
            val s = nullableValue<String>()
        }
    }
    return "OK"
}