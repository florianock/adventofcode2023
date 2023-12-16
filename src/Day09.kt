fun main() { // --- Day 9: Mirage Maintenance ---

    fun getDiffs(values: List<Int>): List<List<Int>> {
        val allDiffs = arrayListOf(values)
        var current = values

        do {
            current = current.zipWithNext().map { it.second - it.first }
            allDiffs.add(current)
        } while (current.any { n -> n != 0 })

        return allDiffs.reversed()
    }

    fun historyValue(values: List<Int>) = getDiffs(values).fold(0) { diff, lst -> lst.first() - diff }
    fun futureValue(values: List<Int>) = getDiffs(values).fold(0) { diff, lst -> lst.last() + diff }

    fun part1(input: List<String>) = input.map { it.split(' ').map { n -> n.toInt() } }.sumOf(::futureValue)
    fun part2(input: List<String>) = input.map { it.split(' ').map { n -> n.toInt() } }.sumOf(::historyValue)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
