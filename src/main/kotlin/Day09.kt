object Day09 : Puzzle {
    private tailrec fun List<Int>.extrapolate(reminder: Int = 0): Int =
        if (all { it == 0 }) reminder
        else diffs().extrapolate(last() + reminder)

    private fun List<Int>.diffs(): List<Int> = zipWithNext { a, b -> b - a }
    private fun List<String>.parse() = map { line -> line.split(" ").map { value -> value.toInt() } }

    override fun part1(input: List<String>) = input.parse().sumOf { it.extrapolate() }
    override fun part2(input: List<String>) = input.parse().sumOf { it.reversed().extrapolate() }
}
