package destructuring

data class Some(val first: Int, val second: Double, val third: String)

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        fun foo(some: Some) {
            var (x, y, z: String) = some

            x++
            y *= 2.0
            z = ""
        }

        fun bar(some: Some) {
            val (a, _, `_`) = some
        }
    }
    return "OK"
}