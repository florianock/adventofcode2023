import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() { // --- Day 6: Wait For It ---
    fun parse(input: List<String>): List<Pair<Long, Long>> {
        val split = input.map { line ->
            """(\d+)""".toRegex().findAll(line).map { m -> m.groupValues[1].toLong() }.toList()
        }
        return split[0].zip(split[1])
    }

    fun calculateWinningStreak(time: Long, record: Long): Pair<Long, Long> {
        // solve: -x^2 + time*x - record = 0
        val low = (time - sqrt((time * time - 4.0 * record))) / 2.0
        var startWinning = ceil(low)
        if (startWinning == low) startWinning += 1.0

        val high = (time + sqrt((time * time - 4.0 * record))) / 2.0
        var endWinning = floor(high)
        if (endWinning == high) endWinning -= 1.0

        return Pair(startWinning.toLong(), endWinning.toLong())
    }

    fun part1(input: List<String>): Int {
        val races = parse(input)
        val result = races.map { (time, record) ->
            val (start, end) = calculateWinningStreak(time, record)
            (end - start + 1L).toInt()
        }
        return result.fold(1) { acc, n -> acc * n }
    }

    fun part2(input: List<String>): Int {
        val race = parse(input).unzip().toList().map { numbers -> numbers.joinToString("").toLong() }
        val (start, end) = calculateWinningStreak(race[0], race[1])
        return (end - start + 1L).toInt()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
