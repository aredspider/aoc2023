object Day16 : Puzzle {
    private infix fun Coord.shine(dir: Coord) = (this + dir) to dir

    override fun part1(input: List<String>) = input.calculateEnergizedCount(0 to 0 to RIGHT).size
    override fun part2(input: List<String>) =
        (((0 to 0) step RIGHT).take(input.width).map { coord -> coord to DOWN } +
                ((0 to 0) step DOWN).take(input.height).map { coord -> coord to RIGHT } +
                (input.bottomRight step LEFT).take(input.width).map { coord -> coord to UP } +
                (input.bottomRight step UP).take(input.height).map { coord -> coord to LEFT })
            .maxOf { input.calculateEnergizedCount(it).size }
    // we could reuse previously calculated energized beams, still it works fast enough

    private fun List<String>.calculateEnergizedCount(start: Pair<Coord, Coord>): MutableMap<Coord, MutableSet<Pair<Coord, Coord>>> {
        val beams = mutableListOf<Pair<Coord, Coord>>()
        beams.add(start)
        val energized = mutableMapOf<Coord, MutableSet<Pair<Coord, Coord>>>()

        while (beams.isNotEmpty()) {
            val current = beams.removeLast()
            val (currentPos, currentDir) = current
            if (!(currentPos isWithinRangeOf this)) // pro KT-5351
                continue
            if (!energized.getOrPut(currentPos, ::mutableSetOf).add(current))
                continue

            val c = this[currentPos]
            when {
                c == '-' && currentDir.isUpOrDown -> {
                    beams.add(currentPos shine LEFT)
                    beams.add(currentPos shine RIGHT)
                }

                c == '|' && currentDir.isLeftOrRight -> {
                    beams.add(currentPos shine UP)
                    beams.add(currentPos shine DOWN)
                }

                c == '/' && currentDir.isUpOrDown || c == '\\' && currentDir.isLeftOrRight ->
                    beams.add(currentPos shine currentDir.cw)

                c == '\\' && currentDir.isUpOrDown || c == '/' && currentDir.isLeftOrRight ->
                    beams.add(currentPos shine currentDir.ccw)

                else -> beams.add(currentPos shine currentDir)
            }
        }
        return energized
    }
}