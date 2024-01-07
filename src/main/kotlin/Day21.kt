import kotlin.math.roundToLong

object Day21 : Puzzle {
    private fun expand(input: List<String>) = sequence {
        var map = input.startCoordSet().toSet()
        var steps = 0

        var oddResult = 0
        var evenResult = 1
        val visited = map.toMutableSet()
        yield(evenResult)

        while (true) {
            steps++
            val next = mutableSetOf<Coord>()
            map.flatMapTo(next) { from -> from.adjacent4.filter { input[it mod input] != '#' && visited.add(it) } }

            if (steps % 2 == 0) {
                evenResult += next.size
                yield(evenResult)
            } else {
                oddResult += next.size
                yield(oddResult)
            }

            map = next
        }
    }

    fun countSteps(input: List<String>, stepGoal: Int) = expand(input).elementAt(stepGoal)

    private fun List<String>.startCoordSet() = setOf(coordsOf { it == 'S' }.single())

    private fun parabola(values: List<Pair<Int, Int>>): Triple<Double, Double, Double> {
        require(values.size == 3)
        val x = values.map { it.x }
        val y = values.map { it.y }

        val denom = ((x[0] - x[1]) * (x[0] - x[2]) * (x[1] - x[2])).toDouble()

        val a = (x[2] * (y[1] - y[0]) + x[1] * (y[0] - y[2]) + x[0] * (y[2] - y[1])) / denom
        val b = (x[2] * x[2] * (y[0] - y[1]) + x[1] * x[1] * (y[2] - y[0]) + x[0] * x[0] * (y[1] - y[2])) / denom
        val c = (x[1] * x[2] * (x[1] - x[2]) * y[0] + x[2] * x[0] * (x[2] - x[0]) * y[1] + x[0] * x[1] * (x[0] - x[1]) * y[2]) / denom

        return Triple(a, b, c)
    }

    private operator fun Triple<Double,Double,Double>.invoke(x: Int) = (first * x * x + second * x + third).roundToLong()

    fun calculateSteps(input: List<String>, stepGoal: Int): Long {
        class StepGoalReachedException(val y: Int) : Exception()

        val values = try {
            expand(input)
                .withIndex()
                .onEach { (x, y) -> if (x == stepGoal) throw StepGoalReachedException(y) }
                .filter { (x, _) -> x % input.width == stepGoal % input.width }
                .windowed(4)
                .first { it.map { (_, v) -> v }.derive().derive().let { ddys -> ddys[0] == ddys[1] } }
                .map { (x, y) -> x to y }
        } catch (e: StepGoalReachedException) {
            return e.y.toLong()
        }

        println(values)
        val f = parabola(values.dropLast(1))
        values.forEach { (x, y) -> println("f($x) = $y = ${f(x)}") }
        return f(stepGoal)
    }

    override fun part1(input: List<String>) = countSteps(input, stepGoal = 64)
    override fun part2(input: List<String>) = calculateSteps(input, stepGoal = 26501365)
}
