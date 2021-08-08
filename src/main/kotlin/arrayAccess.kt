package arrayAccess

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        val p = 0
        fun foo() = 1

        class Wrapper(val v: IntArray)

        fun test(a: IntArray, w: Wrapper) { a[0] + a[p] + a[foo()] + w.v[0] }
    }
    return "OK"
}