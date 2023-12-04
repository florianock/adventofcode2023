typealias ScratchCardCollection = List<String>
typealias WinsCount = Int

fun main() { // --- Day 4: Scratchcards ---
    fun ScratchCardCollection.countWinningNumbersPerCard(): List<WinsCount> {
        return this.map { line ->
            val split = line.replace(Regex("""\s+"""), " ").split(Regex("""(: | \| )"""))
            val winningNumbers = split[1].split(" ")
            winningNumbers.count { n -> n in split[2].split(" ") }
        }
    }

    fun getScore(wins: WinsCount): Int = if (wins < 1) 0 else 2 toPowerOf (wins - 1)

    fun List<WinsCount>.collectAllCards(): IntArray {
        val startCount = this.map { _ ->  1 }.toIntArray()
        return this.foldIndexed (startCount) { current, counter, wins ->
            var nextCard = current + 1
            var cardsToAdd = wins
            while (cardsToAdd-- > 0) counter[nextCard++] += counter[current]
            counter
        }
    }

    fun part1(input: List<String>): Int {
        return input.countWinningNumbersPerCard().sumOf(::getScore)
    }

    fun part2(input: List<String>): Int {
        return input.countWinningNumbersPerCard().collectAllCards().sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
