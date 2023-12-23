import java.util.LinkedList
import java.util.Queue

object Day23 : Puzzle {
    override fun part1(input: List<String>): Any {
        return calculateLongestPath(input, slippery = true)
    }

    override fun part2(input: List<String>): Any {
        return calculateLongestPath(input, slippery = false)
    }

    data class Destination(val end: Coord, val distance: Int, val secondStep: Coord)

    private fun calculateLongestPath(input: List<String>, slippery: Boolean): Any {
        fun Coord.neighbours() = adjacent4
            .filter { nextPos ->
                val field = input.getOrNull(nextPos)
                when {
                    field == '#' || field == null -> false
                    field == '.' || !slippery && (field == '<' || field == '>' || field == '^' || field == 'v') -> true
                    field.arrowDirOrNull == nextPos - this -> true
                    else -> false
                }
            }

        val graph = mutableMapOf<Coord, MutableList<Destination>>()
        val start = 1 to 0
        val goal = input.bottomRight.left

        val q : Queue<Pair<Coord, Coord>> = LinkedList()
        q.add(start to start)

        while(q.isNotEmpty()) {
            val head = q.remove()
            val edgeStart = head.first
            if(graph[edgeStart]?.any { it.secondStep == head.second } == true) continue

            var comingFrom = edgeStart
            var edgeEnd = head.second
            var neighbours: List<Coord>
            var distance = 1
            while(true) {
                neighbours = edgeEnd.neighbours().filter { it != comingFrom }
                if(neighbours.size == 1) {
                    comingFrom = edgeEnd
                    edgeEnd = neighbours.single()
                    distance++
                } else {
                    break
                }
            }
            if(neighbours.isEmpty() && edgeEnd != goal) continue

            graph.getOrPut(edgeStart) { mutableListOf() }.add(Destination(end = edgeEnd, secondStep = head.second, distance = distance))
            q.addAll(neighbours.map { nextEdgeStart -> edgeEnd to nextEdgeStart })
        }

        return (graph.calculateLongestPath(from = start, to = goal) ?: error("could not find path from $start to $goal")) - 1
    }

    private fun Map<Coord, List<Destination>>.calculateLongestPath(from: Coord, to: Coord, visited: Set<Coord> = setOf(from)) : Int? {
        return getValue(from)
            .filter { (end) -> end !in visited }
            .mapNotNull { (end, distance) ->
                if(end != to) calculateLongestPath(from = end, to = to, visited + end)?.plus(distance) else distance
            }.maxOrNull()
    }
}
