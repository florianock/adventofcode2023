typealias Hand = String
typealias Bid = Int

fun main() { // --- Day 7: Camel Cards ---
    data class Game(val hand: Hand, val bid: Bid, val withJokers: Boolean) {
        var type = this.getType(withJokers)

        fun rankType(type: List<Int> = this.type): Int = 5 - type.size + type.first()

        fun getHandComparator(): String =
            this.hand.map { c -> this.getCardValue(c) }.joinToString { i -> i.toString(14) }

        fun getType(withJokers: Boolean): List<Int> =
            if (withJokers) substituteJokers() else typeFromHand(this.hand)

        private fun substituteJokers(): List<Int> {
            val cards = arrayOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
            return if (this.hand.contains('J')) {
                cards.map { c -> typeFromHand(this.hand.replace('J', c)) }.maxBy(::rankType)
            } else {
                typeFromHand(this.hand)
            }
        }

        private fun typeFromHand(hand: Hand): List<Int> =
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
    }

    fun parse(input: List<String>, withJokers: Boolean): List<Game> {
        return input.map { l ->
            l.split(' ').let { arr ->
                Game(arr[0], arr[1].toInt(), withJokers)
            }
        }
    }

    fun part1(input: List<String>): Int {
        val games = parse(input, false)
        val ranked =
            games.sortedWith(compareBy({ it.rankType() }, { it.getHandComparator() }))
        return ranked.mapIndexed { idx, game -> (idx + 1) * game.bid }.sum()
    }

    fun part2(input: List<String>): Int {
        val games = parse(input, true)
        val ranked = games.sortedWith(compareBy({ it.rankType() }, { it.getHandComparator() }))
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
