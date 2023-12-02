import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException

abstract class PuzzleTest {
    protected abstract val puzzle: Puzzle
    protected abstract val expectedSampleResultPart1: Any
    protected open val expectedSampleResultPart2: Any by lazy { TODO("enter sample result for part 2") }

    private fun readFile(fileName: String): String {
        return (this::class.java.getResource(fileName) ?: throw FileNotFoundException(fileName)).readText();
    }

    private val name = this::class.simpleName!!.lowercase().removeSuffix("test")
    private val inputSample1 by lazy { readFile("${name}sample1.txt") }
    private val inputSample2 by lazy { try { readFile("${name}sample2.txt") } catch (e: FileNotFoundException) { inputSample1 } }
    private val input by lazy { readFile("$name.txt") }

    @Test
    fun testSamplePart1() {
        assertThat(puzzle.part1(inputSample1)).isEqualTo(expectedSampleResultPart1)
    }

    @Test
    fun testSamplePart2() {
        assertThat(puzzle.part2(inputSample2)).isEqualTo(expectedSampleResultPart2)
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
