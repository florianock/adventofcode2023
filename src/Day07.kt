fun main() { // --- Day 7: Camel Cards ---
    data class Game(val hand: String, val bid: Int, val withJokers: Boolean) : Comparable<Game> {
        val type = if (withJokers) substituteJokers() else typeFromHand(this.hand)

        private fun rankType(t: List<Int> = this.type): Int = 5 - t.size + t.first()

        private fun getHandComparator(): String =
            hand.map { c -> getCardValue(c) }.joinToString { i -> i.toString(14) }

        private fun substituteJokers(): List<Int> {
            return if (hand.contains('J')) {
                val possibleCards = arrayOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
                possibleCards.map { c -> typeFromHand(hand.replace('J', c)) }.maxBy(::rankType)
            } else {
                typeFromHand(hand)
            }
        }

        private fun typeFromHand(hand: String): List<Int> =
            hand.groupBy { it }.map { (_, v) -> v.size }.sortedDescending()

        private fun getCardValue(x: Char): Int = when {
            x.isDigit() -> x.toString().toInt() - 1
            x == 'T' -> 9
            x == 'J' -> if (withJokers) 0 else 10
            x == 'Q' -> 11
            x == 'K' -> 12
            x == 'A' -> 13
            else -> throw IllegalArgumentException("Unknown card: $x")
        }

        override fun compareTo(other: Game): Int =
            compareValuesBy(this, other, { it.rankType() }, { it.getHandComparator() })
    }

    fun parse(input: List<String>, withJokers: Boolean): List<Game> {
        return input.map { l ->
            l.split(' ').let { arr ->
                Game(arr[0], arr[1].toInt(), withJokers)
            }
        }
    }

    fun part1(input: List<String>): Int = parse(input, false)
        .sorted()
        .mapIndexed { idx, game -> (idx + 1) * game.bid }
        .sum()

    fun part2(input: List<String>): Int = parse(input, true)
        .sorted()
        .mapIndexed { idx, game -> (idx + 1) * game.bid }
        .sum()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
