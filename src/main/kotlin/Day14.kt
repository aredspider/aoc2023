object Day14 : Puzzle {
    data class Platform(val area: List<List<Char>>) {
        companion object {
            fun fromInput(input: List<String>) = Platform(input.toList())
        }

        private val squareRocks: List<Coord> = area.coordsOf { it == '#' }
        private val borders = mapOf(
            WEST to area.indices.map { -1 to it },
            EAST to area.indices.map { area.size to it },
            NORTH to area.first().indices.map { it to -1 },
            SOUTH to area.last().indices.map { it to area.size },
        )

        fun calculateLoad() = area.coordsOf { it == 'O' }.sumOf { (_, y) -> area.size - y }

        fun tilt(direction: Coord): Platform {
            val newArea = area.map { it.toMutableList() }
            (squareRocks + borders.getValue(direction)).forEach { blockingCoord ->
                val count = (blockingCoord step -direction).drop(1)
                    .map { area.getOrNull(it) }
                    .takeWhile { it == 'O' || it == '.' }
                    .count { it == 'O' }

                (blockingCoord step -direction).drop(1).take(count).forEach {
                    newArea[it] = 'O'
                }

                (blockingCoord step -direction).drop(count + 1).takeWhile { area.getOrNull(it).let { c -> c == 'O' || c == '.' } }.forEach {
                    newArea[it] = '.'
                }
            }

            return Platform(newArea)
        }
    }

    override fun part1(input: List<String>): Any {
        return Platform.fromInput(input).tilt(NORTH).calculateLoad()
    }

    override fun part2(input: List<String>): Any {
        val cycles = 1000000000

        var currentPlatform = Platform.fromInput(input)
        var currentCycle = 0
        val previousCycles = mutableMapOf<Platform, Int>()
        var cycleStart: Int? = null

        while (currentCycle < cycles) {
            cycleStart = previousCycles.put(currentPlatform, currentCycle)
            if (cycleStart != null) break
            currentPlatform = currentPlatform.tilt(NORTH).tilt(WEST).tilt(SOUTH).tilt(EAST)
            currentCycle++
        }

        if (cycleStart != null) {
            val cycleLength = currentCycle - cycleStart
            val remainingCycles = cycles - cycleStart
            val cyclesToSkip = remainingCycles % cycleLength
            repeat(cyclesToSkip) {
                currentPlatform = currentPlatform.tilt(NORTH).tilt(WEST).tilt(SOUTH).tilt(EAST)
            }
        }

        return currentPlatform.calculateLoad()
    }
}