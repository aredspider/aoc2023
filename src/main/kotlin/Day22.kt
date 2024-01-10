import java.util.PriorityQueue

object Day22 : Puzzle {
    data class Brick(var lower: Coord3, var upper: Coord3, val name: Char) {
        companion object {
            fun fromString(index: Int, line: String) = line
                .split("~")
                .map { ends -> ends.split(",").map(String::toLong).let { (x, y, z) -> Coord3(x, y, z) } }
                .let { (a, b) -> Brick(lower = min(a, b), upper = max(a, b), 'A' + index % 26) }
        }

        fun movedTo(z: Long): Brick =
            copy(upper = upper.copy(third = upper.z - lower.z + z), lower = lower.copy(third = z))

        private val xRange = lower.x.rangeTo(upper.x)
        private val yRange = lower.y.rangeTo(upper.y)

        infix fun isInPlaneOf(other: Brick) = (
                xRange.contains(other.lower.x) || xRange.contains(other.upper.x)
                        || other.xRange.contains(lower.x) || other.xRange.contains(upper.x)
                ) && (
                yRange.contains(other.lower.y) || yRange.contains(other.upper.y)
                        || other.yRange.contains(lower.y) || other.yRange.contains(upper.y)
                )
    }

    interface PlacedBrick {
        val brick: Brick
        val above: List<PlacedBrick>
        val below: List<PlacedBrick>
        fun wouldFallWithout(removed: Collection<PlacedBrick>) = below.none { it !in removed }
    }

    private fun List<String>.readAndPlaceBricks(): Collection<PlacedBrick> {
        val bricks = mapIndexed(Brick.Companion::fromString)
            .sortedBy { it.lower.z }
            .fold(emptyList<Brick>()) { bricksBelow, brick ->
                val highestZ = bricksBelow.filter { it isInPlaneOf brick }.maxOfOrNull { it.upper.z } ?: 0
                bricksBelow + brick.movedTo(highestZ + 1)
            }

        val aboveCoarse = bricks.groupBy { it.lower.z - 1 }.withDefault { emptyList() }
        val belowCoarse = bricks.groupBy { it.upper.z + 1 }.withDefault { emptyList() }

        val above = bricks.associateWith { aboveCoarse.getValue(it.upper.z).filter { above -> above isInPlaneOf it } }::getValue
        val below = bricks.associateWith { belowCoarse.getValue(it.lower.z).filter { below -> below isInPlaneOf it } }::getValue

        lateinit var placedBricks : Map<Brick, PlacedBrick>

        fun Brick.place(): PlacedBrick =
            object : PlacedBrick {
                override val brick: Brick = this@place
                override val above: List<PlacedBrick> get() = above(this@place).map { placedBricks.getValue(it) }
                override val below: List<PlacedBrick> get() = below(this@place).map { placedBricks.getValue(it) }
            }

        placedBricks = bricks.associateWith { it.place() }

        return placedBricks.values
    }

    override fun part1(input: List<String>) =
        input.readAndPlaceBricks()
            .count { disintegrated ->
                disintegrated.above.none { above -> above.wouldFallWithout(setOf(disintegrated)) }
            }

    override fun part2(input: List<String>) =
        input.readAndPlaceBricks()
            .sumOf { disintegrated ->
                val q = PriorityQueue(Comparator.comparing { it: PlacedBrick -> it.brick.lower.z })
                val wouldFall = mutableSetOf(disintegrated)
                q.addAll(disintegrated.above)

                while (q.isNotEmpty()) {
                    val current = q.remove()
                    if (current in wouldFall) continue
                    if (current.wouldFallWithout(wouldFall)) {
                        wouldFall.add(current)
                        q.addAll(current.above)
                    }
                }

                wouldFall.size - 1
            }
}
