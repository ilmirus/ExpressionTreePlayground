package callableReference

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        class A {
            fun foo() {}
            val bar = 0
        }

        fun A.qux() {}

        fun baz() {}

        val test1 = { A()::foo }

        val test2 = { A()::bar }

        val test3 = { A()::qux }

        val test4 = { A::foo }

        val test5 = { A::bar }

        val test6 = { A::qux }

        val test7 = { ::baz }

        val test8 = { A?::foo }
    }
    return "OK"
}
