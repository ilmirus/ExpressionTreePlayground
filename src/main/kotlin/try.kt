package _try

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        try {
            throw KotlinNullPointerException()
        } catch (e: RuntimeException) {
            println("Runtime exception")
        } catch (e: Exception) {
            println("Some exception")
        } finally {
            println("finally")
        }
    }
    return "OK"
}