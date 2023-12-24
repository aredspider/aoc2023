object Day21 : Puzzle {
    override fun part1(input: List<String>) = countSteps(input, stepGoal = 64)
    override fun part2(input: List<String>) = countSteps(input, stepGoal = 26501365)

    fun countSteps(input: List<String>, stepGoal: Int) : Any {
        val start = input.coordsOf { it == 'S' }.single()
        return (1..stepGoal).fold(setOf(start)) { visited, _ ->
            visited.flatMap { it.adjacent4 }.filter { input[it mod input] != '#' }.toSet()
        }.size
    }
}