package locals

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val test = expressionTree {
        class Local(val pp: Int) {
            val p = 0
            fun diff() = pp - p
        }

        val x = Local(42).diff()

        fun sum(y: Int, z: Int, f: (Int, Int) -> Int): Int {
            return x + f(y, z)
        }

        val code = (object : Any() {
            fun foo() = hashCode()
        }).foo()

        sum(code, Local(1).diff(), fun(x: Int, y: Int) = x + y)
    }
    return "OK"
}