package classReference

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

class A

fun box(): String {
    val tree = expressionTree {
        A::class
        classReference.A::class
        A()::class

        A::class.java
        classReference.A::class.java
        A()::class.java
    }
    return "OK"
}