//--- Day 1: Trebuchet?! ---

fun main() {
    val digitWords = arrayOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    fun getCalibrationValue(line: String): Int {
        val first = line.first { it.isDigit() }
        val last = line.last { it.isDigit() }
        return "$first$last".toInt()
    }

    fun digitOf(d: String): Int = digitWords.indexOf(d) + 1

    fun part1(input: List<String>): Int {
        return input.sumOf { line -> getCalibrationValue(line) }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val parsed = digitWords.fold(line) { acc, d -> acc.replace(d, d + digitOf(d) + d) }
            getCalibrationValue(parsed)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)
    val testInput2 = readInput("Day01_test2")
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
