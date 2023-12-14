class Day12Test : PuzzleTest() {
    override val puzzle = Day12
    override val expectedSampleResultsPart1 = listOf(1, 4, 1, 1, 4, 10)
    override val expectedSampleResultsPart2 = listOf(1, 16384, 1, 16, 2500, 506250)

    override fun readSampleInput(part: Int, sampleIndex: Int): String {
        return readFileOrThrow(getInputFileName(true)).lines().get(sampleIndex)
    }
}