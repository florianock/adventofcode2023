typealias Galaxies = Set<Pair<Long, Long>>

fun main() {

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
        val (rc, cc) = this.unzip()
        return this.map { (a, b) ->
            Pair(a + (0..<a).count { it !in rc } * (factor - 1L),
                b + (0..<b).count { it !in cc } * (factor - 1L))
        }.toSet()
    }

    fun part1(input: List<String>, factor: Int): Long {
        val galaxies = getGalaxies(input)
        return galaxies
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
