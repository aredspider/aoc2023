import kotlin.math.min

object Day13 : Puzzle {
    private fun List<String>.countMirrorLines(expectedMismatch: Int = 0): Int {
        val possibleMirrorPositionsWithMismatch = asSequence().windowed(2).mapIndexedNotNull { index, (l, r) ->
            (index to (l countDifferentCharacters r)).takeIf { (_, mismatch) -> mismatch <= expectedMismatch }
        }

        return possibleMirrorPositionsWithMismatch.filter { (mirrorStart, mismatch) ->
            val checkLength = min(mirrorStart, size - mirrorStart - 2)
            (1..checkLength).sumOf {
                this[mirrorStart - it] countDifferentCharacters this[mirrorStart + it + 1]
            } + mismatch == expectedMismatch
        }.map { (mirrorStart) -> mirrorStart + 1 }.firstOrNull() ?: 0
    }

    private fun String.solve(expectedMismatch: Int = 0) =
        split("\n\n")
            .sumOf { block ->
                val lines = block.lines()
                (100 * lines.countMirrorLines(expectedMismatch = expectedMismatch)).takeIf { it != 0 }
                    ?: lines.transpose().countMirrorLines(expectedMismatch = expectedMismatch)
            }

    override fun part1(input: String) = input.solve()
    override fun part2(input: String) = input.solve(expectedMismatch = 1)
}