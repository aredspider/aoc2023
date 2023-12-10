object Day03 : Puzzle {
    data class Part(val start: Coord, val end: Coord, val number: Int)

    private fun String.parsePart(pos: Coord): Part {
        val (x, y) = pos
        val pre = this.subSequence(0, x + 1).reversed().takeWhile { it.isDigit() }.reversed()
        val post = this.subSequence(x + 1, length).takeWhile { it.isDigit() }
        val number = (pre.toString() + post).toInt()
        return Part((x - pre.length + 1) to y, (x + post.length) to y, number)
    }

    override fun part1(input: List<String>) = input.flatMapIndexed { y: Int, line: String ->
        line.flatMapIndexed { x, c ->
            if (!c.isDigit() && c != '.')
                (x to y).adjacent.filter { input.getOrNull(it)?.isDigit() == true }.map { pos -> input[pos.y].parsePart(pos) }
            else
                emptyList()
        }
    }
        .distinct()
        .sumOf(Part::number)

    override fun part2(input: List<String>) = input.flatMapIndexed { y: Int, line: String ->
        line.mapIndexedNotNull { x, c ->
            if (c == '*') {
                val adjacentParts = (x to y).adjacent.filter { input.getOrNull(it)?.isDigit() == true }
                    .map { pos -> input[pos.y].parsePart(pos) }.distinct()
                if (adjacentParts.size == 2)
                    adjacentParts[0].number * adjacentParts[1].number
                else
                    null
            } else
                null
        }
    }.sum()
}
