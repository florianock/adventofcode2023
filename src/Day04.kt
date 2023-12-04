import kotlin.math.pow

fun main() {
    fun countWinningNumbers(input: List<String>): List<Int> {
        return input.map { line ->
            val split = line.replace(Regex("""\s+"""), " ").split(Regex("""(: | \| )"""))
            val winningNumbers = split[1].split(" ")
            winningNumbers.count { n -> n in split[2].split(" ") }
        }
    }

    fun getScore(winCount: Int): Int = if (winCount < 1) 0 else 2.toDouble().pow(winCount - 1).toInt()

    fun countCards(wins: List<Int>): Int {
        val startCount = wins.map { _ ->  1 }.toIntArray()
        return wins.foldIndexed (startCount) { current, counter, amountWon ->
            var nextCard = current+1
            var cardsToAdd = amountWon
            while (cardsToAdd > 0) {
                counter[nextCard++] += counter[current]
                cardsToAdd--
            }
            counter
        }.sum()
    }

    fun part1(input: List<String>): Int {
        return countWinningNumbers(input).sumOf { p -> getScore(p) }
    }

    fun part2(input: List<String>): Int {
        return countCards(countWinningNumbers(input))
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
