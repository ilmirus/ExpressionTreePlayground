package modifications

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val test = expressionTree {
        fun test() {
            fun simple() {
                var x = 10
                x += 20
                x -= 5
                x /= 5
                x *= 10
            }

            fun List<String>.modify() {
                this += "Alpha"
                this += "Omega"
            }

            fun Any.modify() {
                (this as List<Int>) += 42
            }
        }
    }
    return "OK"
}