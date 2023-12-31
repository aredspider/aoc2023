import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class Day21Test : PuzzleTest() {
    override val puzzle : Day21 = Day21
    override val expectedSampleResultPart1 = 16

    @TestFactory
    override fun testSamplesPart1(): List<DynamicTest> =
        listOf(DynamicTest.dynamicTest("Part 1 Sample") {
            assertThat(puzzle.countSteps(readSampleInput(part = 1, sampleIndex = 0).lines(), stepGoal = 6)).isEqualTo(expectedSampleResultPart1)
        })

    @TestFactory
    override fun testSamplesPart2(): List<DynamicTest> = listOf(
        6 to 16L,
        10 to 50L,
        50 to 1594L,
        100 to 6536L,
        500 to 167004L,
        1000 to 668697L,
        5000 to 16733044L,
    ).map { (steps, plots) ->
        DynamicTest.dynamicTest("Part 2 Sample $steps steps") {
            assertThat(puzzle.calculateSteps(readSampleInput(part = 2, sampleIndex = 0).lines(), stepGoal = steps)).isEqualTo(plots)
        }
    }
}
