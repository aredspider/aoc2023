import kotlin.math.absoluteValue

typealias Coord = Pair<Int, Int>

inline val Coord.x get() = first
inline val Coord.y get() = second

fun List<String>.getOrNull(pos: Coord): Char? = getOrNull(pos.y)?.getOrNull(pos.x)
infix fun Coord.isWithinRangeOf(area: List<String>): Boolean = y in area.indices && x in area[y].indices
operator fun List<String>.get(pos: Coord): Char = get(pos.y)[pos.x]

operator fun Coord.plus(other: Coord): Coord = x + other.x to y + other.y
operator fun Coord.minus(other: Coord): Coord = x - other.x to y - other.y
operator fun Coord.times(t: Int) = x * t to y * t

val LEFT = -1 to 0
val RIGHT = 1 to 0
val UP = 0 to -1
val DOWN = 0 to 1
val LEFT_UP = -1 to -1
val LEFT_DOWN = -1 to 1
val RIGHT_UP = 1 to -1
val RIGHT_DOWN = 1 to 1

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

fun Coord.manhattanDistance(to: Coord) = (x - to.x).absoluteValue + (y - to.y).absoluteValue

fun List<String>.withCoords() =
    flatMapIndexed { y: Int, row: String ->
        row.mapIndexed { x: Int, value: Char ->
            (x to y) to value
        }
    }

infix fun Int.exclProgressionTo(b: Int) = if (this > b) (this - 1) downTo (b + 1) else (this + 1)..<b

fun List<String>.transpose() =
    first().indices.map { x ->
        this.indices.map { y ->
            get(x to y)
        }.joinToString(separator = "")
    }

fun <T> Iterable<T>.indicesOf(predicate: (T) -> Boolean) =
    withIndex().filter { (_, value) -> predicate(value) }.map { (index, _) -> index }

fun <T> Sequence<T>.indicesOf(predicate: (T) -> Boolean) =
    withIndex().filter { (_, value) -> predicate(value) }.map { (index, _) -> index }

fun <T> Collection<T>.replace(i: Int, newValue: T): List<T> = require(i in indices).let { take(i) + newValue + drop(i + 1) }
fun String.replace(i: Int, newValue: Char) : String = require(i in indices).let { take(i) + newValue + drop(i + 1) }

infix fun String.countDifferentCharacters(to: String) = require(this.length == to.length).let { zip(to).count { (l, r) -> l != r } }

fun debugPrint(data: Collection<Collection<Any?>>) {
    data.forEach { row ->
        row.forEach(::print)
        println()
    }
}
