package _for

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

data class Some(val x: Int, val y: Int)

fun box(): String {
    val tree = expressionTree {
        fun foo() {
            for (i in 1..10) {
                println(i)
            }
        }

        fun fooLabeled() {
            println("!!!")
            label@ for (i in 1..10) {
                if (i == 5) continue@label
                println(i)
            }
            println("!!!")
        }

        fun bar(list: List<String>) {
            for (element in list.subList(0, 10)) {
                println(element)
            }
            for (element in list.subList(10, 20)) println(element)
        }

        fun baz(set: Set<Some>) {
            for ((x, y) in set) {
                println("x = $x y = $y")
            }
        }

        fun withParameter(list: List<Some>) {
            for (s: Some in list) {
                println(s)
            }
        }
    }
    return "OK"
}