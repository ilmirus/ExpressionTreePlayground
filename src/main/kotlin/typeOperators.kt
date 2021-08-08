package typeOperators

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

interface IThing

fun box(): String {
    val tree = expressionTree {
        fun test1(x: Any) = x is IThing
        fun test2(x: Any) = x !is IThing
        fun test3(x: Any) = x as IThing
        fun test4(x: Any) = x as? IThing
    }
    return "OK"
}