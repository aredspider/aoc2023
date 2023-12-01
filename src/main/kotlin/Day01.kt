object Day01 : Puzzle {
    override fun part1(input: List<String>) = input.sumOf { "${it.first(Char::isDigit)}${it.last(Char::isDigit)}".toInt() }

    private val numbers = listOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
    private val numberRegex = "([0-9]|${numbers.joinToString("|")})".toRegex()
    private fun String.fromStringNumberToInt(): Int = if(length == 1) toInt() else numbers.indexOf(this)

    // source https://discuss.kotlinlang.org/t/feature-request-regex-findall-with-overlap/27729
    private fun Regex.findAllWithOverlap(input: CharSequence, startIndex: Int = 0): Sequence<MatchResult> {
        if (startIndex < 0 || startIndex > input.length) {
            throw IndexOutOfBoundsException("Start index out of bounds: $startIndex, input length: ${input.length}")
        }
        return generateSequence({ find(input, startIndex) }, { find(input, it.range.first + 1) })
    }

    override fun part2(input: List<String>): Int {
        return input.sumOf {
            numberRegex.findAllWithOverlap(it).let { matches ->
                val first = matches.first().value.fromStringNumberToInt()
                val last = matches.last().value.fromStringNumberToInt()
                val i = (first * 10) + last
                i
            }
        }
    }
}
