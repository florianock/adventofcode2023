typealias Hand = String
typealias Bid = Int

fun main() { // --- Day 7: Camel Cards ---

    data class Game(val hand: Hand, val bid: Bid, val withJokers: Boolean) {
        var type = this.getType(withJokers)

        fun getRank(type: List<Int> = this.type): Int = type.size - type.first() + 5

        private fun getType(withJokers: Boolean): List<Int> {
            return if (withJokers) {
                substituteJokers()
            } else {
                typeFromHand(this.hand)
            }
        }

        private fun substituteJokers(): List<Int> {
            val cards = arrayOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
            return if (this.hand.contains('J')) {
                cards.map { c -> typeFromHand(this.hand.replace('J', c)) }.minBy(::getRank)
            } else {
                typeFromHand(this.hand)
            }
        }

        private fun typeFromHand(hand: Hand): List<Int> =
            hand.groupBy { it }.map { (_, v) -> v.size }.sortedDescending()
    }


    fun parse(input: List<String>, withJokers: Boolean): List<Game> {
        return input.map { l ->
            l.split(' ').let { arr ->
                Game(arr[0], arr[1].toInt(), withJokers)
            }
        }
    }

    fun getCardValue(x: Char, withJokers: Boolean): Int {
        return when {
            x.isDigit() -> x.toString().toInt()
            x == 'T' -> 10
            x == 'J' -> if (withJokers) 1 else 11
            x == 'Q' -> 12
            x == 'K' -> 13
            x == 'A' -> 14
            else -> throw IllegalArgumentException("Unknown card: $x")
        }
    }

    fun compareHands(a: String, b: String, withJokers: Boolean): Int {
        return a.zip(b).firstOrNull { (x, y) -> x != y }
            ?.let { (x, y) -> getCardValue(x, withJokers).compareTo(getCardValue(y, withJokers)) } ?: 0
    }

    fun compareCamelCards(withJokers: Boolean) = { a: Game, b: Game ->
        when (val result = b.getRank().compareTo(a.getRank())) {
            0 -> compareHands(a.hand, b.hand, withJokers)
            else -> result
        }
    }

    fun part1(input: List<String>): Int {
        val games = parse(input, false)
        val ranked = games.sortedWith(compareCamelCards(false))
        return ranked.mapIndexed { idx, game -> (idx + 1) * game.bid }.sum()
    }

    fun part2(input: List<String>): Int {
        val games = parse(input, true)
        val ranked = games.sortedWith(compareCamelCards(true))
        return ranked.mapIndexed { idx, game -> (idx + 1) * game.bid }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
