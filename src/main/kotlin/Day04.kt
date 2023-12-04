import kotlin.math.pow

object Day04 : Puzzle {
    private val numberRegex = Regex("\\d+")
    private fun String.numberOfWins(): Int {
        val (winning, numbers) = substringAfter(": ")
            .split(" | ")
            .map { numberList -> numberRegex.findAll(numberList).map { numberMatch -> numberMatch.value.toInt() } }
        val winningSet = winning.toSet()
        return numbers.count { it in winningSet }
    }

    override fun part1(input: List<String>) =
        input.sumOf { line ->
            line.numberOfWins()
                .takeIf { it > 0 }
                ?.let { 2.0.pow(it - 1).toInt() }
                ?: 0
        }

    override fun part2(input: List<String>): Any {
        val numberOfCards = input.map { 1 }.toMutableList()
        input.forEachIndexed { i, line ->
            ((i + 1)..(i + line.numberOfWins())).forEach { numberOfCards[it] += numberOfCards[i] }
        }
        return numberOfCards.sum()
    }
}
