object Day08 : Puzzle {
    private fun walk(
        startNodes: List<String>,
        instructions: List<Int>,
        map: Map<String, List<String>>,
        moveOffset: Long = 0,
        destinationPredicate: (String) -> Boolean
    ): Long {
        var moves = moveOffset
        val visited = startNodes.map { mutableMapOf((0L to it) to moves) }
        val cycleLengths = startNodes.map { 0L }.toMutableList()
        var currentNodes = startNodes
        while (cycleLengths.any { it == 0L }) {
            if (currentNodes.all(destinationPredicate))
                return moves - moveOffset
            currentNodes = currentNodes.map { currentNode -> map.getValue(currentNode)[instructions[(moves % instructions.size).toInt()]] }
            moves++

            cycleLengths.withIndex()
                .filter { (_, cycleLength) -> cycleLength == 0L }
                .forEach { (index, _) ->
                    val previousVisit = visited[index].put(moves % instructions.size to currentNodes[index], moves)
                    if (previousVisit != null) {
                        cycleLengths[index] = moves - previousVisit
                    }
                }
        }
        return cycleLengths.lcm()
    }

    private fun List<String>.parseInput(): Pair<List<Int>, Map<String, List<String>>> {
        val instructions = first().map {
            when (it) {
                'L' -> 0
                'R' -> 1
                else -> error("Not a valid direction: $it")
            }
        }

        val mapRegex = """([A-Z0-9]{3}) = \(([A-Z0-9]{3}), ([A-Z0-9]{3})\)""".toRegex()
        val map = drop(2).map { mapRegex.matchEntire(it)!!.destructured }.associate { (at, l, r) -> at to listOf(l, r) }

        return instructions to map
    }

    override fun part1(input: List<String>): Any {
        val (instructions, map) = input.parseInput()
        return walk(listOf("AAA"), instructions, map) { it == "ZZZ" }
    }

    override fun part2(input: List<String>): Any {
        val (instructions, map) = input.parseInput()
        return walk(map.keys.filter { it.endsWith("A") }, instructions, map) { it.endsWith("Z") }
    }
}
