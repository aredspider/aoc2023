object Day12 : Puzzle {
    private fun List<String>.parse(): Pair<List<String>, List<Int>> = let { (springs, count) ->
        return "[#?]+".toRegex().findAll(springs).map { it.value }.toList() to count.split(",").map { it.toInt() }
    }

    private fun String.unfold(separator: Char) = "$this$separator$this$separator$this$separator$this$separator$this"

    private val cache = mutableMapOf<Pair<List<String>, List<Int>>, Long>()
    private fun Pair<List<String>, List<Int>>.findArrangements(): Long = cache.computeIfAbsent(this) { (springs, count) ->
        if (count.isEmpty() && springs.all { spring -> spring.all { it == '?' } }) return@computeIfAbsent 1L
        if (count.isEmpty() || springs.isEmpty()) return@computeIfAbsent 0L

        val firstSpring = springs.first()
        val firstCount = count.first()

        val restSprings = springs.drop(1)
        val restCount = count.drop(1)

        var arrangement = 0L

        if (firstSpring.all { it == '?' }) { // spring is okay, test when skip
            arrangement += (restSprings to count).findArrangements()
        }

        firstSpring.windowedSequence(firstCount)
            .takeUntil { it.first() == '#' }
            .indices
            .filter { index ->
                firstSpring.getOrNull(index + firstCount) != '#' // defect spring must end here
            }
            .forEach { index ->
                val rest = firstSpring.drop(index + firstCount + 1) // + 1 for space
                arrangement += if (rest.isEmpty())
                    (restSprings to restCount).findArrangements()
                else
                    (listOf(rest) + restSprings to restCount).findArrangements()
            }

        arrangement
    }

    override fun part1(input: List<String>): Any {
        return input.sumOf { it.split(" ").parse().findArrangements() }
    }

    override fun part2(input: List<String>): Any {
        return input.sumOf { line ->
            line.split(" ")
                .let { (springs, count) -> listOf(springs.unfold('?'), count.unfold(',')) }
                .parse()
                .findArrangements()
        }
    }
}
