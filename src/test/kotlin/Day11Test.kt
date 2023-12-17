import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class Day11Test : PuzzleTest() {
    override val puzzle = Day11
    override val expectedSampleResultPart1 = 374L

    @TestFactory
    override fun testSamplesPart2(): List<DynamicTest> =
        listOf(10L to 1030L, 100L to 8410L)
            .mapIndexed { sampleIndex, (dilutionFactor, expected) ->
                DynamicTest.dynamicTest("Part 2 Sample ${sampleIndex + 1}") {
                    assertThat(
                        puzzle.calculateDistancesBetweenGalaxies(
                            readSampleInput(part = 2, sampleIndex).lines(),
                            dilutionFactor
                        )
                    ).isEqualTo(expected)
                }
            }
}