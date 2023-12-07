import kotlin.math.max
import kotlin.math.min

object Day07 : Puzzle {
    data class Hand(val cards: String, val bid: Int, val jokersMode: Boolean) : Comparable<Hand> {
        companion object {
            fun fromString(s: String, jokersMode: Boolean = false): Hand =
                s.split(" ").let { (cards, bid) -> Hand(cards, bid.toInt(), jokersMode) }
        }

        enum class Score(val matches: (List<Int>, Int) -> Boolean) {
            HighCard({ _, _ -> true }),
            OnePair({ (biggestCardCount), jokersCount -> biggestCardCount + jokersCount >= 2 }),
            TwoPairs({ (biggestCardCount, secondBiggestCardCount), jokersCount -> biggestCardCount + jokersCount >= 2 && secondBiggestCardCount + max(0, jokersCount - (2 - min(biggestCardCount, 2))) >= 2 }),
            ThreeOfAKind({ (biggestCardCount), jokersCount -> biggestCardCount + jokersCount >= 3 }),
            FullHouse({ (biggestCardCount, secondBiggestCardCount), jokersCount -> biggestCardCount + jokersCount >= 3 && secondBiggestCardCount + max(0, jokersCount - (3 - min(biggestCardCount, 3))) >= 2 }),
            FourOfAKind({ (biggestCardCount), jokersCount -> biggestCardCount + jokersCount >= 4 }),
            RoyalFlush({ (biggestCardCount), jokersCount -> biggestCardCount + jokersCount >= 5 });

            companion object {
                fun calcScore(cards: String, jokersMode: Boolean): Score {
                    val cardsCount = cards.groupBy { it }.mapValues { (_, cards) -> cards.size }
                    return cardsCount.map { (type, count) -> if (type == 'J' && jokersMode) 0 else count }.sortedDescending()
                        .let { sortedCardCount ->
                            entries.last { score -> score.matches(sortedCardCount, cardsCount['J']?.takeIf { jokersMode } ?: 0) }
                        }
                }
            }
        }

        private val score = Score.calcScore(cards, jokersMode)

        override fun compareTo(other: Hand): Int =
            score.compareTo(other.score).takeUnless { it == 0 } ?: compareCardByCard(other)

        private fun Char.value() = when (this) {
            in '2'..'9' -> this - '0'
            'T' -> 10
            'J' -> if (jokersMode) 1 else 11
            'Q' -> 12
            'K' -> 13
            'A' -> 14
            else -> throw IllegalArgumentException("Invalid card value $this")
        }

        private fun compareCardByCard(other: Hand): Int {
            cards.zip(other.cards).first { (m, o) -> m != o }.let { (m, o) -> return m.value().compareTo(o.value()) }
        }

    }

    override fun part1(input: List<String>) = input.map(Hand::fromString)
        .sorted()
        .mapIndexed { index, hand -> hand.bid * (index + 1) }
        .sum()

    override fun part2(input: List<String>) = input.map { Hand.fromString(it, jokersMode = true) }
        .sorted()
        .mapIndexed { index, hand -> hand.bid * (index + 1) }
        .sum()
}