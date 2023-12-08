enum class HandType(val order: Int) {
    FIVE_OF_A_KIND(6),
    FOUR_OF_A_KIND(5),
    FULL_HOUSE(4),
    THREE_OF_A_KIND(3),
    TWO_PAIR(2),
    ONE_PAIR(1),
    HIGH_CARD(0),
    ;

    companion object {
        fun parseHandType(hand: String): HandType {
            val cardCounts = hand.groupingBy { it }.eachCount().values.sorted()
            return when(cardCounts) {
                listOf(1, 1, 1, 1, 1) -> HIGH_CARD
                listOf(1, 1, 1, 2) -> ONE_PAIR
                listOf(1, 2, 2) -> TWO_PAIR
                listOf(1, 1, 3) -> THREE_OF_A_KIND
                listOf(2, 3) -> FULL_HOUSE
                listOf(1, 4) -> FOUR_OF_A_KIND
                listOf(5) -> FIVE_OF_A_KIND
                else -> error("Can't parse hand $hand")
            }
        }

        fun parseHandTypeWithJoker(hand: String): HandType {
            if ('J' !in hand) {
                return parseHandType(hand)
            }

            val cardCount = hand.replace("J", "").groupingBy { it }.eachCount().values.sorted()
            return when (cardCount.size) {
                0,
                1,
                -> FIVE_OF_A_KIND
                2 -> when (cardCount) {
                    // X YYY J
                    listOf(1, 3) -> FOUR_OF_A_KIND
                    // XX YY J
                    listOf(2, 2) -> FULL_HOUSE
                    // X YY JJ
                    listOf(1, 2) -> FOUR_OF_A_KIND
                    // X Y JJJ
                    listOf(1, 1) -> FOUR_OF_A_KIND
                    else -> error("Impossible")
                }
                // XX Y Z J
                // X Y Z JJ
                3 -> THREE_OF_A_KIND
                4 -> ONE_PAIR
                else -> error("Impossible")
            }
        }
    }
}

data class Hand(
    val hand: String,
    val type: HandType,
    private val toNum: (Char) -> Int,
) : Comparable<Hand> {
    override fun compareTo(other: Hand): Int {
        return when {
            type.order != other.type.order -> {
                type.order - other.type.order
            }
            else -> hand.zip(other.hand).asSequence()
                .map { (a, b) -> toNum(a) - toNum(b) }
                .firstOrNull { it != 0 } ?: 0
        }
    }

    companion object {
        private fun toNum(value: Char): Int {
            return when (value) {
                'T' -> 10
                'J' -> 11
                'Q' -> 12
                'K' -> 13
                'A' -> 14
                else -> value.digitToInt()
            }
        }

        private fun toNumWithJoker(value: Char): Int {
            return when (value) {
                'T' -> 10
                'Q' -> 12
                'K' -> 13
                'A' -> 14
                'J' -> 1
                else -> value.digitToInt()
            }
        }

        fun fromString(hand: String): Hand {
            return Hand(hand, HandType.parseHandType(hand), ::toNum)
        }

        fun fromStringWithJoker(hand: String): Hand {
            return Hand(hand, HandType.parseHandTypeWithJoker(hand), ::toNumWithJoker)
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.associate {
            val (hand, bid) = it.split(" ")
            Hand.fromString(hand) to bid.toInt()
        }.toSortedMap().asSequence()
            .mapIndexed { i, (_, v) ->  (i + 1) * v}
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input.associate {
            val (hand, bid) = it.split(" ")
            Hand.fromStringWithJoker(hand) to bid.toInt()
        }.toSortedMap().asSequence()
            .mapIndexed { i, (_, v) ->  (i + 1) * v}
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
