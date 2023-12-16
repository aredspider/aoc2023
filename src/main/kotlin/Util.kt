import kotlin.math.absoluteValue

typealias Coord = Pair<Int, Int>

inline val Coord.x get() = first
inline val Coord.y get() = second

fun List<String>.getOrNull(pos: Coord): Char? = getOrNull(pos.y)?.getOrNull(pos.x)
fun <T> List<List<T>>.getOrNull(pos: Coord): T? = getOrNull(pos.y)?.getOrNull(pos.x)
infix fun Coord.isWithinRangeOf(area: List<String>): Boolean = y in area.indices && x in area[y].indices
operator fun List<String>.get(pos: Coord): Char = get(pos.y)[pos.x]

operator fun <T> List<MutableList<T>>.set(coord: Pair<Int, Int>, value: T) {
    this[coord.y][coord.x] = value
}

operator fun Coord.plus(other: Coord): Coord = x + other.x to y + other.y
operator fun Coord.minus(other: Coord): Coord = x - other.x to y - other.y
operator fun Coord.times(t: Int) = x * t to y * t
operator fun Coord.unaryMinus() = -x to -y

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

val Coord.left get() = this + LEFT
val Coord.right get() = this + RIGHT
val Coord.up get() = this + UP
val Coord.down get() = this + DOWN
val Coord.leftUp get() = this + LEFT_UP
val Coord.leftDown get() = this + LEFT_DOWN
val Coord.rightUp get() = this + RIGHT_UP
val Coord.rightDown get() = this + RIGHT_DOWN

val Coord.adjacent get() = listOf(left, right, up, down, leftUp, leftDown, rightUp, rightDown)
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
val List<String>.height get() = size
val List<String>.bottomRight get() = last().length - 1 to size - 1

fun List<String>.coordsOf(predicate: (Char) -> Boolean) =
    withCoords().filter { (_, value) -> predicate(value) }.map { (coord, _) -> coord }

@JvmName("listCoordsOf")
fun <T> List<List<T>>.coordsOf(predicate: (T) -> Boolean) =
    withCoords().filter { (_, value) -> predicate(value) }.map { (coord, _) -> coord }

infix fun Coord.step(direction: Coord) = generateSequence(this) { it + direction }

infix fun Int.exclProgressionTo(b: Int) = if (this > b) (this - 1) downTo (b + 1) else (this + 1)..<b

fun List<String>.transpose() =
    first().indices.map { x ->
        this.indices.map { y ->
            get(x to y)
        }.joinToString(separator = "")
    }

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
