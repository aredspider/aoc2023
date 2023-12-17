import java.util.Comparator
import java.util.PriorityQueue

object Day17 : Puzzle {
    override fun part1(input: List<String>): Any {
        return input.solve(maxSteps = 3)
    }

    override fun part2(input: List<String>): Any {
        return input.solve(minSteps = 4, maxSteps = 10)
    }

    private fun List<String>.solve(minSteps: Int = 1, maxSteps: Int): Int {
        val heatMap = map { it.map { c -> c.digitToInt() } }

        data class Path(val dirSteps: Int, val dir: Coord?, val heatLoss: Int, val coords: List<Coord>) {
            operator fun plus(nextDir: Coord) = (currentPos + nextDir).let { nextPos ->
                Path(if (dir == nextDir) dirSteps + 1 else 1, nextDir, heatLoss + heatMap[nextPos], coords + nextPos)
            }
            val currentPos get() = coords.last()
        }

        val q = PriorityQueue(Comparator.comparing(Path::heatLoss))
        q.add(Path(0, null, 0, listOf(0 to 0)))
        val visitedNodes = mutableSetOf<Any>()

        while (true) {
            val path = q.poll() ?: error("No path found")
            if (path.currentPos == heatMap.bottomRight && path.dirSteps >= minSteps) {
                forEachIndexed { y, line -> line.forEachIndexed { x, c -> if (x to y in path.coords) print('X') else print(c) }; println() }
                println()
                return path.heatLoss
            }
            if (!visitedNodes.add(path.dirSteps to path.dir to path.currentPos)) continue

            when (path.dirSteps) {
                in 1..<minSteps -> listOf(path.dir!!)
                maxSteps -> UP_RIGHT_DOWN_LEFT.filter {
                    it != path.dir
                }

                else -> UP_RIGHT_DOWN_LEFT
            }.filter { nextDir -> -nextDir != path.dir && path.currentPos + nextDir isWithinRangeOf heatMap }
                .map { path + it }
                .forEach { nextPath ->
                    q.add(nextPath)
                }
        }
    }
}