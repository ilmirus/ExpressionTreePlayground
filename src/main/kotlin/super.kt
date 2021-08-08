package _super

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}
interface A {
    fun foo() {}
}

interface B {
    fun foo() {}
    fun bar() {}
}

fun box(): String {
    val tree = expressionTree {
        class C : A, B {
            override fun bar() {
                super.bar()
            }

            override fun foo() {
                super<A>.foo()
                super<B>.foo()
            }
        }
    }
    return "OK"
}