fun main() { // --- Day 9: Mirage Maintenance ---

    fun getAllDiffs(values: List<Long>): List<List<Long>> {
        val allDiffs = arrayListOf(values)
        var cur = values

        while (cur.any { n -> n != 0L }) {
            cur = cur.zipWithNext().map { (a, b) -> b - a }
            allDiffs.add(cur)
        }

        return allDiffs.reversed()
    }

    fun historyValue(values: List<Long>): Long = getAllDiffs(values).fold(0L) { diff, lst -> lst.first() - diff }

    fun futureValue(values: List<Long>): Long = getAllDiffs(values).fold(0L) { diff, lst -> lst.last() + diff }

    fun part1(input: List<String>): Long {
        return input
            .map { l -> l.split(' ').map { n -> n.toLong() } }
            .sumOf(::futureValue)
    }

    fun part2(input: List<String>): Long {
        return input
            .map { l -> l.split(' ').map { n -> n.toLong() } }
            .sumOf(::historyValue)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114L)
    check(part2(testInput) == 2L)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
