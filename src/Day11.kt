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
        val result = mutableSetOf<Pair<Long, Long>>()
        val (galaxyRows, galaxyCols) = this.unzip().let { Pair(it.first.toSet(), it.second.toSet()) }
        for ((a, b) in this) {
            result.add(
                Pair(
                    a + ((0..<a) - galaxyRows).size * (factor - 1L),
                    b + ((0..<b) - galaxyCols).size * (factor - 1L)
                )
            )
        }
        return result
    }

    fun part1(input: List<String>, factor: Int) = getGalaxies(input)
        .expand(factor)
        .allPairs()
        .sumOf { (a, b) -> manhattanDistance(a, b) }

    fun part2(input: List<String>, factor: Int) = part1(input, factor)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput, 2) == 374L)
    check(part2(testInput, 10) == 1030L)
    check(part2(testInput, 100) == 8410L)

    val input = readInput("Day11")
    part1(input, 2).println()
    part2(input, 1_000_000).println()
}
