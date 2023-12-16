fun main() { // --- Day 7: Camel Cards ---

    fun parse(input: List<String>, withJokers: Boolean) =
        input.map { l -> l.split(' ').let { CamelCardGame(it[0], it[1].toInt(), withJokers) } }

    fun part1(input: List<String>) = parse(input, false)
        .sorted()
        .mapIndexed { idx, game -> (idx + 1) * game.bid }
        .sum()

    fun part2(input: List<String>) = parse(input, true)
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

data class CamelCardGame(private val cards: String, val bid: Int, private val withJokers: Boolean) :
    Comparable<CamelCardGame> {
    private val type = if (withJokers) substituteJokers() else getType(cards)

    companion object {
        val PossibleCards = arrayOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
    }

    private fun rank(t: List<Int> = this.type) = 5 - t.size + t.first()

    private fun hand() = cards.map(::getBase14Value).joinToString("")

    private fun substituteJokers() =
        if (cards.contains('J'))
            PossibleCards.map { c -> getType(cards.replace('J', c)) }.maxBy(::rank)
        else
            getType(cards)

    private fun getType(hand: String) = hand.groupBy { it }.map { (_, v) -> v.size }.sortedDescending()

    private fun getBase14Value(x: Char) = when {
        x.isDigit() -> x - 1
        x == 'T' -> '9'
        x == 'J' -> if (withJokers) '0' else 'a'
        x == 'Q' -> 'b'
        x == 'K' -> 'c'
        x == 'A' -> 'd'
        else -> throw IllegalArgumentException("Unknown card: $x")
    }

    override fun compareTo(other: CamelCardGame) = compareValuesBy(this, other, { it.rank() }, { it.hand() })
}
