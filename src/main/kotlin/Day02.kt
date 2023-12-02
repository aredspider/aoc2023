object Day02 : Puzzle {
    data class Cubes(val r: Int, val g: Int, val b: Int) {
        companion object {
            fun fromString(s: String): Cubes {
                val map = s.split(",").associate {
                    val (count, color) = it.trim()
                        .split(" ")
                    color to count.toInt()
                }
                return Cubes(r = map["red"] ?: 0, g = map["green"] ?: 0, b = map["blue"] ?: 0)
            }
        }

        infix fun allLessThanEqual(other: Cubes): Boolean {
            return r <= other.r && g <= other.g && b <= other.b
        }
    }

    data class Game(val n: Int, val cubes: List<Cubes>) {
        companion object {
            fun fromInput(input: List<String>) = input.map { fromLine(it) }
            private fun fromLine(s: String): Game {
                val (game, cubesList) = s.split(":")
                val n = game.removePrefix("Game ").toInt()
                return Game(n, cubesList.split(";").map { Cubes.fromString(it.trim()) })
            }
        }
    }

    override fun part1(input: List<String>) : Any {
        val games = Game.fromInput(input)
        val ref = Cubes(r = 12, g = 13, b = 14)
        return games.filter {
            it.cubes.all { cube -> cube allLessThanEqual ref }
        }.sumOf { it.n }
    }

    override fun part2(input: List<String>) =
        Game.fromInput(input)
            .sumOf { it.cubes.maxOf(Cubes::r) * it.cubes.maxOf(Cubes::g) * it.cubes.maxOf(Cubes::b) }
}
