import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

typealias Coord = Pair<Int, Int>
typealias LCoord = Pair<Long, Long>
typealias Coord3 = Triple<Long, Long, Long>

inline val Coord.x get() = first
inline val Coord.y get() = second
inline val LCoord.x get() = first
inline val LCoord.y get() = second
inline val Coord3.x get() = first
inline val Coord3.y get() = second
inline val Coord3.z get() = third

fun List<String>.getOrNull(pos: Coord): Char? = getOrNull(pos.y)?.getOrNull(pos.x)
fun <T> List<List<T>>.getOrNull(pos: Coord): T? = getOrNull(pos.y)?.getOrNull(pos.x)
infix fun Coord.isWithinRangeOf(area: List<String>): Boolean = y in area.indices && x in area[y].indices
infix fun Coord.mod(area: List<String>): Coord = this mod area.size2

@JvmName("listIsWithinRangeOf")
infix fun Coord.isWithinRangeOf(area: List<List<*>>): Boolean = y in area.indices && x in area[y].indices
operator fun List<String>.get(pos: Coord): Char = get(pos.y)[pos.x]
operator fun <T> List<List<T>>.get(pos: Coord): T = get(pos.y)[pos.x]

operator fun <T> List<MutableList<T>>.set(coord: Pair<Int, Int>, value: T) {
    this[coord.y][coord.x] = value
}

operator fun Coord.plus(other: Coord): Coord = x + other.x to y + other.y
operator fun Coord.minus(other: Coord): Coord = x - other.x to y - other.y
operator fun Coord.times(t: Int) = x * t to y * t
operator fun Coord.unaryMinus() = -x to -y
operator fun Coord.times(t: Long) = x * t to y * t
infix fun Coord.mod(other: Coord) = x.mod(other.x) to y.mod(other.y)

@JvmName("LCoordPlus")
operator fun LCoord.plus(other: LCoord): LCoord = x + other.x to y + other.y

@JvmName("LCoordMinus")
operator fun LCoord.minus(other: LCoord): LCoord = x - other.x to y - other.y

@JvmName("LCoordTimes")
operator fun LCoord.times(t: Long) = x * t to y * t

@JvmName("LCoordUnaryMinus")
operator fun LCoord.unaryMinus() = -x to -y

val LEFT = -1 to 0
val RIGHT = 1 to 0
val UP = 0 to -1
val DOWN = 0 to 1
val LEFT_UP = -1 to -1
val LEFT_DOWN = -1 to 1
val RIGHT_UP = 1 to -1
val RIGHT_DOWN = 1 to 1

val NORTH = UP
val SOUTH = DOWN
val WEST = LEFT
val EAST = RIGHT

val UP_RIGHT_DOWN_LEFT = listOf(UP, RIGHT, DOWN, LEFT)

val Coord.left get() = this + LEFT
val Coord.right get() = this + RIGHT
val Coord.up get() = this + UP
val Coord.down get() = this + DOWN
val Coord.leftUp get() = this + LEFT_UP
val Coord.leftDown get() = this + LEFT_DOWN
val Coord.rightUp get() = this + RIGHT_UP
val Coord.rightDown get() = this + RIGHT_DOWN

val Char.arrowDirOrNull
    get() = when (this) {
        '^' -> UP
        'v' -> DOWN
        '<' -> LEFT
        '>' -> RIGHT
        else -> null
    }

val Coord.adjacent8 get() = listOf(left, right, up, down, leftUp, leftDown, rightUp, rightDown)
val Coord.adjacent4 get() = listOf(left, right, up, down)
val Coord.inverted get() = -x to -y
val Coord.ccw get() = y to -x
val Coord.cw get() = -y to x
val Coord.isUpOrDown get() = x == 0
val Coord.isLeftOrRight get() = y == 0

fun Coord.manhattanDistance(to: Coord) = (x - to.x).absoluteValue + (y - to.y).absoluteValue

fun List<String>.withCoords() =
    flatMapIndexed { y: Int, row: String ->
        row.mapIndexed { x: Int, value: Char ->
            (x to y) to value
        }
    }

@JvmName("listWithCoords")
fun <T> List<List<T>>.withCoords() =
    flatMapIndexed { y: Int, row: List<T> ->
        row.mapIndexed { x: Int, value: T ->
            (x to y) to value
        }
    }

val List<String>.width get() = first().length
val List<*>.height get() = size
val List<String>.size2 get() = last().length to size
val List<String>.bottomRight get() = last().length - 1 to size - 1

@get:JvmName("listWidth")
val List<List<*>>.width get() = first().size

@get:JvmName("listBottomRight")
val List<List<*>>.bottomRight get() = last().size - 1 to size - 1

fun List<String>.coordsOf(predicate: (Char) -> Boolean) =
    withCoords().filter { (_, value) -> predicate(value) }.map { (coord, _) -> coord }

@JvmName("listCoordsOf")
fun <T> List<List<T>>.coordsOf(predicate: (T) -> Boolean) =
    withCoords().filter { (_, value) -> predicate(value) }.map { (coord, _) -> coord }

infix fun Coord.step(direction: Coord) = generateSequence(this) { it + direction }

fun min(a: Coord3, b: Coord3) = Triple(min(a.x, b.x), min(a.y, b.y), min(a.z, b.z))
fun max(a: Coord3, b: Coord3) = Triple(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z))

infix fun Int.exclProgressionTo(b: Int) = if (this > b) (this - 1) downTo (b + 1) else (this + 1)..<b

fun List<String>.transpose() =
    first().indices.map { x ->
        this.indices.map { y ->
            get(x to y)
        }.joinToString(separator = "")
    }

fun <T> List<T>.countIndexed(predicate: (Int, T) -> Boolean) = withIndex().count { (index, value) -> predicate(index, value) }
fun List<String>.toMutableCharList() = map { it.toMutableList() }.toMutableList()
fun List<String>.toList() = map { it.toList() }

fun String.indicesOf(predicate: (Char) -> Boolean) =
    withIndex().filter { (_, value) -> predicate(value) }.map { (index, _) -> index }

fun <T> Iterable<T>.indicesOf(predicate: (T) -> Boolean) =
    withIndex().filter { (_, value) -> predicate(value) }.map { (index, _) -> index }

fun <T> Sequence<T>.indicesOf(predicate: (T) -> Boolean) =
    withIndex().filter { (_, value) -> predicate(value) }.map { (index, _) -> index }

fun <T> List<T>.replace(i: Int, newValue: T): List<T> = require(i in indices).let { take(i) + newValue + drop(i + 1) }
fun String.replace(i: Int, newValue: Char): String = require(i in indices).let { take(i) + newValue + drop(i + 1) }

infix fun String.countDifferentCharacters(to: String) = require(this.length == to.length).let { zip(to).count { (l, r) -> l != r } }

fun <T> Sequence<T>.takeUntil(predicate: (T) -> Boolean) = sequence {
    this@takeUntil.forEach {
        yield(it)
        if (predicate(it)) return@sequence
    }
}

val Sequence<Any?>.indices get() = mapIndexed { index, _ -> index }

fun <T> List<List<T>>.debugPrint() = onEach { row ->
    row.forEach(::print)
    println()
}

@JvmName("debugPrintString")
fun List<String>.debugPrint() = onEach { row ->
    row.forEach(::print)
    println()
}

fun List<Long>.lcm() = reduce { acc, n -> (acc * n).absoluteValue / (acc gcd n) }
infix fun Long.lcm(other: Long) = (this * other).absoluteValue / (this gcd other)
infix fun Long.gcd(other: Long): Long = if (other == 0L) this else other gcd (this % other)
fun crt(values: List<Pair<Long, Long>>): Pair<Long, Long>? =
    values.reduceOrNull { (result, lcm), (a, n) ->
        val t = a % n
        ((0L..<n).asSequence().map { result + lcm * it }.firstOrNull { it % n == t } ?: return null) to (lcm lcm n)
    }

fun List<Int>.derive() = zipWithNext().map { (a, b) -> b - a }