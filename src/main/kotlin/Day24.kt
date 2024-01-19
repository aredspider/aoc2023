import kotlin.math.roundToLong

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

    private fun List<Hailstone>.findIntersections2(): List<Triple<DCoord, Hailstone, Hailstone>> =
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

    override fun part2(input: List<String>): Long {
        val hailstones = input.take(4).map(Hailstone::fromString)

        //////////////////////////////////////////////////////////////
        // p0 + t[i] * v0 = p[i] + t[i] * v[i]
        // (p0 - p[i]) / (v[i] - v0) = t[i]
        // (px0 - px[i]) / (vx[i] - vx0) = (py0 - py[i]) / (vy[i] - vy0)
        // (px0 - px[i]) * (vy[i] - vy0) = (py0 - py[i]) * (vx[i] - vx0)
        // px0 * vy[i] - px0 * vy0 - px[i] * vy[i] + px[i] * vy0 = py0 * vx[i] - py0 * vx0 - py[i] * vx[i] + py[i] * vx0
        // px0 * vy[i] - px[i] * vy[i] + px[i] * vy0 - py0 * vx[i] + py[i] * vx[i] - py[i] * vx0 = px0 * vy0 - py0 * vx0
        // vy[i] * px0 - vx[i] * py0 - py[i] * vx0 + px[i] * vy0 + (py[i] * vx[i] - px[i] * vy[i]) = px0 * vy0 - py0 * vx0
        // vy[0] * px0 - vx[0] * py0 - py[0] * vx0 + px[0] * vy0 + (py[0] * vx[0] - px[0] * vy[0]) = vy[1] * px0 - vx[1] * py0 - py[1] * vx0 + px[1] * vy0 + (py[1] * vx[1] - px[1] * vy[1])

        // (vy[0]-vy[1]) * px0 + (-vx[0]+vx[1]) * py0 + 0*pz0                + (-py[0]+py[1]) * vx0 + (px[0]-px[1]) * vy0 + 0*vz0               = px[0]*vy[0]-px[1]*vy[1]-py[0]*vx[0]+py[1]*vx[1]
        // (vz[0]-vz[1]) * px0 + 0*py0                + (-vx[0]+vx[1]) * pz0 + (-pz[0]+pz[1]) * vx0 + 0*vy0               + (px[0]-px[1]) * vz0 = px[0]*vz[0]-px[1]*vz[1]-pz[0]*vx[0]+pz[1]*vx[1]
        // (vy[0]-vy[2]) * px0 + (-vx[0]+vx[2]) * py0 + 0*pz0                + (-py[0]+py[2]) * vx0 + (px[0]-px[2]) * vy0 + 0*vz0               = px[0]*vy[0]-px[2]*vy[2]-py[0]*vx[0]+py[2]*vx[2]
        // (vz[0]-vz[2]) * px0 + 0*py0                + (-vx[0]+vx[2]) * pz0 + (-pz[0]+pz[2]) * vx0 + 0*vy0               + (px[0]-px[2]) * vz0 = px[0]*vz[0]-px[2]*vz[2]-pz[0]*vx[0]+pz[2]*vx[2]
        // (vy[0]-vy[3]) * px0 + (-vx[0]+vx[3]) * py0 + 0*pz0                + (-py[0]+py[3]) * vx0 + (px[0]-px[3]) * vy0 + 0*vz0               = px[0]*vy[0]-px[3]*vy[3]-py[0]*vx[0]+py[3]*vx[3]
        // (vz[0]-vz[3]) * px0 + 0*py0                + (-vx[0]+vx[3]) * pz0 + (-pz[0]+pz[3]) * vx0 + 0*vy0               + (px[0]-px[3]) * vz0 = px[0]*vz[0]-px[3]*vz[3]-pz[0]*vx[0]+pz[3]*vx[3]

        val px = hailstones.map { it.p.x.toDouble() }
        val py = hailstones.map { it.p.y.toDouble() }
        val pz = hailstones.map { it.p.z.toDouble() }
        val vx = hailstones.map { it.v.x.toDouble() }
        val vy = hailstones.map { it.v.y.toDouble() }
        val vz = hailstones.map { it.v.z.toDouble() }
        val b = vec(
            px[0] * vy[0] - px[1] * vy[1] - py[0] * vx[0] + py[1] * vx[1],
            px[0] * vz[0] - px[1] * vz[1] - pz[0] * vx[0] + pz[1] * vx[1],
            px[0] * vy[0] - px[2] * vy[2] - py[0] * vx[0] + py[2] * vx[2],
            px[0] * vz[0] - px[2] * vz[2] - pz[0] * vx[0] + pz[2] * vx[2],
            px[0] * vy[0] - px[3] * vy[3] - py[0] * vx[0] + py[3] * vx[3],
            px[0] * vz[0] - px[3] * vz[3] - pz[0] * vx[0] + pz[3] * vx[3],
        )
        val m: Matrix = listOf(
            listOf(vy[0] - vy[1], -vx[0] + vx[1], 0.0, -py[0] + py[1], px[0] - px[1], 0.0),
            listOf(vz[0] - vz[1], 0.0, -vx[0] + vx[1], -pz[0] + pz[1], 0.0, px[0] - px[1]),
            listOf(vy[0] - vy[2], -vx[0] + vx[2], 0.0, -py[0] + py[2], px[0] - px[2], 0.0),
            listOf(vz[0] - vz[2], 0.0, -vx[0] + vx[2], -pz[0] + pz[2], 0.0, px[0] - px[2]),
            listOf(vy[0] - vy[3], -vx[0] + vx[3], 0.0, -py[0] + py[3], px[0] - px[3], 0.0),
            listOf(vz[0] - vz[3], 0.0, -vx[0] + vx[3], -pz[0] + pz[3], 0.0, px[0] - px[3]),
        )
        val result: Matrix = m.inverse() * b
        return result.col(0).take(3).sumOf { it.roundToLong() }
    }
}
