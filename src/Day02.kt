fun main() { // --- Day 2: Cube Conundrum ---
    fun getValueForGame(grabbed: IntArray): Int {
        if (arrayOf(12, 13, 14).mapIndexed{ idx, available -> available >= grabbed[idx+1] }.all{ it }) {
            return grabbed[0]
        }
        return 0
    }

    fun getMinimumSetOfCubes(line: String): IntArray {
        val game = line.split(Regex("""[:,;]?\s""")).chunked(2).groupBy { it[1] }
        val id = game.keys.first().toInt()
        return arrayOf("red", "green", "blue").foldIndexed(intArrayOf(id,0,0,0)) { idx, acc, color ->
            val max = game[color]?.maxOfOrNull { l -> l[0].toInt() }
            if (max != null) acc[idx+1] = max
            acc
        }
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val cubes = getMinimumSetOfCubes(line)
            getValueForGame(cubes)
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val cubes = getMinimumSetOfCubes(line)
            cubes[1] * cubes[2] * cubes[3]
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
