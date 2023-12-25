import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class Day24Test : PuzzleTest() {
    override val puzzle = Day24
    override val expectedSampleResultPart1 = 2

    @TestFactory
    override fun testSamplesPart1(): List<DynamicTest> =
        listOf(DynamicTest.dynamicTest("Part 1 Sample") {
            assertThat(puzzle.part1(readSampleInput(part = 1, sampleIndex = 0).lines(), 7L..27L)).isEqualTo(expectedSampleResultPart1)
        })
}
