package calls

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        infix fun Int.distance(y: Int) { this + y }

        fun test() { 3 distance 4 }

        fun testRegular() { 3.distance(4) }

        class My(var x: Int) {
            operator fun invoke() = x

            fun foo() {}

            fun copy() = My(x)
        }

        fun testInvoke() { My(13)() }

        fun testQualified(first: My, second: My?) {
            println(first.x)
            println(second?.x)
            first.foo()
            second?.foo()
            first.copy().foo()
            first.x = 42
        }
    }
    return "OK"
}