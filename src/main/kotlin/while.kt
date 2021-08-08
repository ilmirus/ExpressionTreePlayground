package _while

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        fun foo(limit: Int) {
            var k = 0
            some@ while (k < limit) {
                k++
                println(k)
                while (k == 13) {
                    k++
                    if (k < limit) break@some
                    if (k > limit) continue
                }
            }
        }

        fun bar(limit: Int) {
            var k = limit
            do {
                k--
                println(k)
            } while (k >= 0)
        }
    }
    return "OK"
}