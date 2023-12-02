interface Puzzle {
    fun part1(input: String): Any = part1(input.lines())
    fun part1(input: List<String>): Any
    fun part2(input: String): Any = part2(input.lines())
    fun part2(input: List<String>): Any = TODO("Implement part 2")
}
