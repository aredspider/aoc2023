import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

object Day06 : Puzzle {
    private val numberRegex = """(\d+)""".toRegex()
    private fun String.findNumbers() = numberRegex.findAll(this).map { it.value.toLong() }
    private fun List<String>.parseInput() = first().findNumbers().zip(last().findNumbers())

    private fun String.filterDigits() = filter(Char::isDigit).toLong()
    private fun List<String>.parseInput2() = first().filterDigits() to last().filterDigits()

    private fun Pair<Long, Long>.calcWinVariants() = let { (raceTime, raceDistance) ->
        val winStart = floor((raceTime - sqrt((raceTime * raceTime - 4.0 * raceDistance))) / 2.0).toLong() + 1
        val winEnd = ceil((raceTime + sqrt(raceTime * raceTime - 4.0 * raceDistance)) / 2.0).toLong() - 1
        winEnd - winStart + 1
    }

    override fun part1(input: List<String>): Any = input.parseInput().map { it.calcWinVariants() }.reduce(Long::times)

    override fun part2(input: List<String>): Any = input.parseInput2().calcWinVariants()
}