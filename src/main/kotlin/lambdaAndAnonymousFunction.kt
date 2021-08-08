package lambdaAndAnonymousFunction

inline fun <T> run(block: () -> T): T = block()

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        fun test_1() {
            run { return@run }
            run { return }
        }

        fun test_2() {
            run(fun (): Int { return 1 })
        }
    }
    return "OK"
}