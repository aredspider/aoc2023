import kotlin.math.absoluteValue

object Day18 : Puzzle {
    data class Instruction(val steps: Long, val dir: Coord) {
        companion object {
            fun fromInput(line: String): Instruction = line.split(" ").let { (dirStr, stepsStr) ->
                val dir = when (dirStr) {
                    "U" -> UP
                    "D" -> DOWN
                    "L" -> LEFT
                    "R" -> RIGHT
                    else -> error("Unknown direction: $dirStr")
                }
                val steps = stepsStr.toLong()
                Instruction(steps, dir)
            }

            fun fromHex(line: String): Instruction = line.substringAfter("#").chunked(5).let { (stepsStr, dirStr) ->
                val dir = when (dirStr[0]) {
                    '0' -> RIGHT
                    '1' -> DOWN
                    '2' -> LEFT
                    '3' -> UP
                    else -> error("Unknown direction: $dirStr")
                }
                val steps = stepsStr.toLong(16)
                Instruction(steps, dir)
            }
        }
    }

    override fun part1(input: List<String>) =
        input.map(Instruction::fromInput).solve()

    override fun part2(input: List<String>) =
        input.map(Instruction::fromHex).solve()

    private fun List<Instruction>.solve(): Long {
        val coordinates = fold(listOf(0L to 0L)) { path, (steps, dir) ->
            path + (path.last() + dir * steps)
        }

        if (coordinates.first() != coordinates.last()) {
            error("not a loop")
        }
        val perimeter = sumOf { (steps, _) -> steps }

        return (coordinates.zipWithNext().sumOf { (a, b) -> (b.x - a.x) * (b.y + a.y) }.absoluteValue + perimeter) / 2L + 1L
    }
}