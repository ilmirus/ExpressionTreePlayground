package annotated

@Target(AnnotationTarget.EXPRESSION, AnnotationTarget.LOCAL_VARIABLE)
@Retention(AnnotationRetention.SOURCE)
annotation class Ann

fun expressionTree(block: () -> Unit) {
    TODO("intrinsic")
}

fun box(): String {
    val tree = expressionTree {
        fun foo(arg: Int): Int {
            if (@Ann arg == 0) {
                @Ann return 1
            }
            @Ann if (arg == 1) {
                return (@Ann 1)
            }
            return 42
        }

        data class Two(val x: Int, val y: Int)

        fun bar(two: Two) {
            val (@Ann x, @Ann y) = two
        }
    }
    return "OK"
}