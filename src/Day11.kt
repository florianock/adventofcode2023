fun main() {
    fun getGalaxies(space: List<String>): Set<Pair<Int, Int>> {
        val result = mutableSetOf<Pair<Int, Int>>()
        for (r in space.indices) {
            for (c in space[r].indices) {
                if (space[r][c] == '#') {
                    result.add(Pair(r, c))
                }
            }
        }
        return result
    }

    fun part1(input: List<String>, factor: Int): Long {
        val galaxies = getGalaxies(input)
        return galaxies
            .map { (a, b) ->
                Pair(a + (0..<a).count { it !in galaxies.unzip().first } * (factor - 1L),
                    b + (0..<b).count { it !in galaxies.unzip().second } * (factor - 1L))
            }
            .allPairs()
            .sumOf { (a, b) -> manhattanDistance(a, b) }
    }

    fun part2(input: List<String>, factor: Int): Long {
        return part1(input, factor)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput, 2) == 374L)
    check(part1(testInput, 10) == 1030L)
    check(part1(testInput, 100) == 8410L)

    val input = readInput("Day11")
    part1(input, 2).println()
    part2(input, 1_000_000).println()
}
