fun main() { // --- Day 9: Mirage Maintenance ---

    fun getAllDiffs(values: List<Int>): List<List<Int>> {
        val allDiffs = arrayListOf(values)
        var cur = values

        while (cur.any { n -> n != 0 }) {
            cur = cur.zipWithNext().map { (a, b) -> b - a }
            allDiffs.add(cur)
        }

        return allDiffs.reversed()
    }

    fun historyValue(values: List<Int>): Int = getAllDiffs(values).fold(0) { diff, lst -> lst.first() - diff }

    fun futureValue(values: List<Int>): Int = getAllDiffs(values).fold(0) { diff, lst -> lst.last() + diff }

    fun part1(input: List<String>): Int {
        return input
            .map { l -> l.split(' ').map { n -> n.toInt() } }
            .sumOf(::futureValue)
    }

    fun part2(input: List<String>): Int {
        return input
            .map { l -> l.split(' ').map { n -> n.toInt() } }
            .sumOf(::historyValue)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
