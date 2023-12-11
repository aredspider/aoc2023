object Day11 : Puzzle {
    override fun part1(input: List<String>): Any {
        return calculateDistancesBetweenGalaxies(input, 2)
    }

    override fun part2(input: List<String>): Any {
        return calculateDistancesBetweenGalaxies(input, 1000000)
    }

    fun calculateDistancesBetweenGalaxies(input: List<String>, dilutionFactor: Long): Long {
        val emptyRows = input.indices.filter { y -> input[y].all { it == '.' } }.toSet()
        val emptyColumns = input.first().indices.filter { x -> input.indices.all { y -> input[x to y] == '.' } }.toSet()

        val galaxyCoords = input.withCoords()
            .filter { (_, c) -> c == '#' }
            .map(Pair<Pair<Int, Int>, Char>::first)

        return galaxyCoords.sumOf { galaxy ->
            galaxyCoords
                .takeWhile { otherGalaxy -> otherGalaxy !== galaxy }
                .sumOf { otherGalaxy ->
                    val crossedDilutions = (galaxy.x exclProgressionTo otherGalaxy.x).count { x -> x in emptyColumns } +
                            (galaxy.y exclProgressionTo otherGalaxy.y).count { y -> y in emptyRows }
                    galaxy.manhattanDistance(otherGalaxy) + (dilutionFactor - 1) * crossedDilutions

                }
        }
    }
}