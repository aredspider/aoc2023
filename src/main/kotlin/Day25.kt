import java.util.LinkedList
import java.util.Queue

object Day25 : Puzzle {
    private fun Map<String, Set<String>>.getAllConnectedNodes(node: String): Set<String> {
        val q: Queue<String> = LinkedList()
        q.add(node)
        val visited = mutableSetOf<String>()
        while (q.isNotEmpty()) {
            val current = q.poll()
            if (visited.add(current))
                q.addAll(getValue(current))
        }
        return visited
    }

    private fun Map<String, Set<String>>.biPartition(): Pair<Set<String>, Set<String>>? {
        val a = getAllConnectedNodes(keys.first())
        if(a.size == keys.size)
            return null
        val b = getAllConnectedNodes(keys.first { it !in a })
        return (a to b).takeIf { a.size + b.size == keys.size }
    }

    private fun Map<String, Set<String>>.minusEdges(vararg edges: Pair<String, String>): Map<String, Set<String>> =
        toMutableMap().also {
            edges.forEach { (a, b) ->
                it[a] = it.getValue(a).minus(b)
                it[b] = it.getValue(b).minus(a)
            }
        }

    private fun Map<String, Set<String>>.edges(): List<Pair<String, String>> =
        keys.flatMap { a ->
            getValue(a).map { b ->
                a to b
            }
        }

    override fun part1(input: List<String>): Any {
        val aGraph: Map<String, MutableSet<String>> = input.associate {
            val (name, neighbours) = it.split(": ")
            name to neighbours.split(" ").toMutableSet()
        }

        val dGraph = aGraph.toMutableMap()
        aGraph.forEach { (name, neighbours) ->
            neighbours.forEach { neighbour ->
                dGraph.getOrPut(neighbour, ::mutableSetOf).add(name)
            }
        }

        val edges = dGraph.edges().withIndex()

        edges.forEach { (i, ab) ->
            edges.drop(i + 1).forEach { (j, cd) ->
                edges.drop(j + 1).forEach { (_, ef) ->
                    dGraph.minusEdges(ab, cd, ef).biPartition()?.let { (a, b) ->
                        return a.size * b.size
                    }
                }
            }
        }

        error("No solution found")
    }
}