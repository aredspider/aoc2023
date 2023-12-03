fun List<String>.getOrNull(pos: Pair<Int, Int>): Char? = getOrNull(pos.second)?.getOrNull(pos.first)

fun Pair<Int, Int>.adjacent() = listOf(
    first - 1 to second - 1,
    first to second - 1,
    first + 1 to second - 1,
    first - 1 to second,
    first + 1 to second,
    first - 1 to second + 1,
    first to second + 1,
    first + 1 to second + 1
)
