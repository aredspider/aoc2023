import java.util.Stack
import kotlin.math.absoluteValue

object Day10 : Puzzle {
    private val nav = mapOf(
        '|' to listOf(UP, DOWN),
        '-' to listOf(LEFT, RIGHT),
        'S' to listOf(UP, DOWN, LEFT, RIGHT),
        'L' to listOf(UP, RIGHT),
        'J' to listOf(UP, LEFT),
        '7' to listOf(DOWN, LEFT),
        'F' to listOf(DOWN, RIGHT),
        '.' to emptyList(),
    )

    private fun List<String>.nextOptions(from: Coord, comingFrom: Coord?) =
        nav.getValue(this[from]).mapNotNull { dir ->
            (from + dir)
                .takeIf { dest ->
                    dest isWithinRangeOf this
                            && dest != comingFrom
                            && nav.getValue(this[dest]).any { destDir -> destDir.inverted == dir }
                }
        }

    private fun List<String>.getLoop(): List<Coord> {
        val start = withIndex().firstNotNullOf { (y, line) ->
            line.indexOf('S').takeIf { it >= 0 }?.let { x -> x to y }
        }

        val stack = Stack<List<Coord>>()
        stack.push(listOf(start))
        while (true) {
            val walked = stack.pop()
            val pos = walked.last()

            if (this[pos] == 'S' && walked.size > 1) {
                return walked
            }

            stack.addAll(nextOptions(pos, walked.getOrNull(walked.size - 2)).map { walked + it })
        }
    }

    override fun part1(input: List<String>) = input.getLoop().size / 2

    override fun part2(input: List<String>) = input.getLoop().let { loop ->
        (loop.zipWithNext().sumOf { (a, b) -> (b.x - a.x) * (b.y + a.y) }.absoluteValue - loop.size + 3) / 2
    }
}
