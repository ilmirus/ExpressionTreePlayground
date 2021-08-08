package qualifierWithTypeArguments

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        Array<String>::class
    }
    return "OK"
}