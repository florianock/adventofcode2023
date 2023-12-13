typealias Galaxies = Set<Pair<Long, Long>>

fun main() { // --- Day 11: Cosmic Expansion ---
    fun getGalaxies(space: List<String>): Galaxies {
        val result = mutableSetOf<Pair<Long, Long>>()
        for (r in space.indices) {
            for (c in space[r].indices) {
                if (space[r][c] == '#') {
                    result.add(Pair(r.toLong(), c.toLong()))
                }
            }
        }
        return result
    }

    fun Galaxies.expand(factor: Int): Galaxies {
        val (rc, cc) = this.unzip().let { (rs, cs) -> Pair(rs.toSet(), cs.toSet()) }
        val result = mutableSetOf<Pair<Long, Long>>()
        for ((a, b) in this) {
            result.add(
                Pair(
                    a + ((0..<a) - rc).size * (factor - 1L),
                    b + ((0..<b) - cc).size * (factor - 1L)
                )
            )
        }
        return result
    }

    fun part1(input: List<String>, factor: Int): Long {
        return getGalaxies(input)
            .expand(factor)
            .allPairs()
            .sumOf { (a, b) -> manhattanDistance(a, b) }
    }

    fun part2(input: List<String>, factor: Int): Long {
        return part1(input, factor)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput, 2) == 374L)
    check(part2(testInput, 10) == 1030L)
    check(part2(testInput, 100) == 8410L)

    val input = readInput("Day11")
    part1(input, 2).println()
    part2(input, 1_000_000).println()
}
