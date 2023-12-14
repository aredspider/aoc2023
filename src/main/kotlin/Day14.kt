object Day14 : Puzzle {
    override fun part1(input: List<String>): Any {
        return input.transpose().sumOf { column ->
            (column.indicesOf { it == '#' } + -1).sumOf { cubeIndex ->
                val count = column
                    .drop(cubeIndex + 1)
                    .takeWhile { it != '#' }
                    .count { it == 'O' }
                val offset = input.size - cubeIndex
                (-count * (count - 2 * offset + 1)) / 2
            }
        }
    }
}