object Day25 : Puzzle {
    class Graph(val nodes: MutableSet<String> = mutableSetOf(), val edges: MutableList<Pair<String, String>> = mutableListOf()) {
        private fun edges(n: String) = edges.filter { (a, b) -> n == a || n == b }.toSet()
        private fun neighbours(n: String) = edges.mapNotNull { (a, b) -> if (n == a) b else if (n == b) a else null }
        fun copy() = Graph(nodes.toMutableSet(), edges.toMutableList())

        private fun removeNode(n: String) {
            nodes.remove(n)
            edges -= edges(n)
        }

        fun mergeNodes(a: String, b: String) {
            val n = a + b
            nodes += n
            edges += (neighbours(a) + neighbours(b)).map { n to it }
            removeNode(a)
            removeNode(b)
        }
    }

    override fun part1(input: List<String>): Any {
        val oGraph = Graph()
        input.forEach {
            val (name, neighbours) = it.split(": ")
            oGraph.nodes += name
            neighbours.split(" ").forEach { neighbour ->
                oGraph.edges += name to neighbour
            }
        }

        while(true) {
            val graph = oGraph.copy()
            while (graph.nodes.size > 2) {
                val (a, b) = graph.edges.random()
                graph.mergeNodes(a, b)
            }
            if (graph.edges.size == 3)
                return graph.nodes.toList().let { (groupA, group) -> groupA.length * group.length / 9 }
        }
    }
}