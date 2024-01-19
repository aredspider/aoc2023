import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UtilKtTest {
    @Test
    fun inverseTest() {
        assertThat(
            listOf(
                listOf(1.0, 2.0),
                listOf(3.0, 4.0),
            ).inverse()
        ).isEqualTo(
            listOf(
                listOf(-2.0, 1.0),
                listOf(1.5, -0.5),
            )
        )
    }

    @Test
    fun determinantTest() {
        assertThat(
            listOf(
                listOf(1.0, 2.0),
                listOf(3.0, 4.0),
            ).determinant()
        ).isEqualTo(-2.0)
    }
}
