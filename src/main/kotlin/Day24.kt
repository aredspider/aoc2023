object Day24 : Puzzle {
    data class Hailstone(
        val p: Coord3,
        val v: Coord3
    ) {
        companion object {
            fun fromString(line: String) =
                line.split("@")
                    .map { it.split(",").map(String::trim).map(String::toLong).let { (x, y, z) -> Coord3(x, y, z) } }
                    .let { (p, v) -> Hailstone(p, v) }
        }

        infix fun findIntersection2Time(other: Hailstone): Double? {
            val x1 = p.x
            val x2 = p.x + v.x
            val x3 = other.p.x
            val x4 = other.p.x + other.v.x
            val y1 = p.y
            val y2 = p.y + v.y
            val y3 = other.p.y
            val y4 = other.p.y + other.v.y

            val denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
            if (denom == 0L)
                return null
            return (((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denom.toDouble()).takeIf { it >= 0.0 }
        }

        fun positionAfter(time: Double): DCoord3 = DCoord3(p.x + time * v.x, p.y + time * v.y, p.z + time * v.z)
        override fun toString(): String {
            return "${p.x}, ${p.y}, ${p.z} @ ${v.x}, ${v.y}, ${v.z}"
        }
    }

    override fun part1(input: List<String>): Any = part1(input, 200000000000000.0..400000000000000.0)

    private fun List<Hailstone>.findIntersections2() : List<Triple<DCoord, Hailstone, Hailstone>> =
        flatMapIndexed { i, a ->
            drop(i + 1).mapNotNull { b ->
                a.findIntersection2Time(b)?.takeIf { b.findIntersection2Time(a) != null }
                    ?.let { a.positionAfter(it) }
                    ?.let { (x, y) -> Triple(DCoord(x, y), a, b) }
            }
        }

    fun part1(input: List<String>, testArea: ClosedFloatingPointRange<Double>): Int =
        input.map(Hailstone::fromString)
            .findIntersections2()
            .filter { (coord) -> coord.x in testArea && coord.y in testArea }
            .size
}