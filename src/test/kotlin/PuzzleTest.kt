import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.io.FileNotFoundException

abstract class PuzzleTest {
    protected abstract val puzzle: Puzzle
    protected open val expectedSampleResultPart1: Any get() = TODO("Provide sample result for part 1")
    protected open val expectedSampleResultsPart1: List<Any> get() = listOf(expectedSampleResultPart1)
    protected open val expectedSampleResultPart2: Any get() = TODO("Provide sample result for part 2")
    protected open val expectedSampleResultsPart2: List<Any> get() = listOf(expectedSampleResultPart2)

    private fun readFile(fileName: String): String? = this::class.java.getResource(fileName)?.readText()
    private fun readFileOrThrow(fileName: String): String = readFile(fileName) ?: throw FileNotFoundException(fileName)

    private val name = this::class.simpleName!!.lowercase().removeSuffix("test")
    private fun getInputFileName(sample: Boolean = false, part: Int? = null, sampleNumber: Int? = null) =
        "$name${if (sample) "sample" else ""}${part ?: ""}${(sampleNumber?.takeIf { part != null }) ?: ""}.txt"

    private fun readSampleInput(part: Int, sampleIndex: Int) =
        readFile(getInputFileName(true, part, sampleIndex + 1))
            ?: readFile(getInputFileName(true, part))
            ?: readFileOrThrow(getInputFileName(true))

    private val input by lazy { readFileOrThrow(getInputFileName()) }

    @TestFactory
    fun testSamplesPart1() =
        expectedSampleResultsPart1.mapIndexed { sampleIndex, expected ->
            dynamicTest("Part 1 Sample ${sampleIndex + 1}") {
                assertThat(puzzle.part1(readSampleInput(part = 1, sampleIndex))).isEqualTo(expected)
            }
        }

    @TestFactory
    fun testSamplePart2() =
        expectedSampleResultsPart2.mapIndexed { sampleIndex, expected ->
            dynamicTest("Part 2 Sample ${sampleIndex + 1}") {
                assertThat(puzzle.part2(readSampleInput(part = 2, sampleIndex))).isEqualTo(expected)
            }
        }

    @Test
    fun part1() {
        println("Answer 1:")
        println(puzzle.part1(input))
    }

    @Test
    fun part2() {
        println("Answer 2:")
        println(puzzle.part2(input))
    }
}
