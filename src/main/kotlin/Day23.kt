import java.util.Stack

object Day23 : Puzzle {
    override fun part1(input: List<String>): Any {
        return input.calculateLongestPath(slippery = true)
    }

    override fun part2(input: List<String>): Any {
        return input.calculateLongestPath(slippery = false)
    }

    private fun List<String>.debugPrint(path: Iterable<Coord>) {
        println("length: ${path.count()}")
        forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if(Coord(x, y) in path) print('O')
                else print(c)
            }
            println()
        }
        println()
    }

    private fun List<String>.calculateLongestPath(slippery: Boolean): Any {
        val q: Stack<List<Coord>> = Stack()
        q.add(listOf(Coord(1, 0)))
        val visited = mutableMapOf<Coord, Int>()
        while (q.isNotEmpty()) {
            val currentPath = q.pop()
            val currentPos = currentPath.last()

            if(currentPos == bottomRight.left) {
                debugPrint(currentPath)
            }

            if(visited[currentPos]?.let { visitedPathLength -> visitedPathLength >= currentPath.size } == true)
                continue
            visited += currentPos to currentPath.size

            val nextPaths = currentPos.adjacent4
                .filter { nextPos -> nextPos !in currentPath  }
                .filter { nextPos ->
                    val field = getOrNull(nextPos)
                    when {
                        field == '.' || !slippery && (field == '<' || field == '>' || field == '^' || field == 'v') -> true
                        field?.arrowDirOrNull == nextPos - currentPos -> true
                        else -> false
                    }
                }.map { nextPos -> currentPath + nextPos }

            q.addAll(nextPaths)
        }

        return visited[bottomRight.left]?.let { pathLength -> pathLength - 1 } ?: error("Could not reach exit")
    }
}
