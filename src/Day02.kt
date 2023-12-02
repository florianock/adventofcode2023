fun main() { // --- Day 2: Cube Conundrum ---
    fun minimumSetOfCubes(game: List<String>): IntArray {
        val grouped = game.chunked(2).groupBy { it[1] }
        return arrayOf("red", "green", "blue").foldIndexed(intArrayOf(game[1].toInt(),0,0,0)) { idx, acc, color ->
            val maxAmount = grouped[color]?.maxOfOrNull { l -> l[0].toInt() }
            if (maxAmount != null) acc[idx+1] = maxAmount
            acc
        }
    }

    fun getValueForGame(cubes: IntArray): Int {
        val availableCubes = arrayOf(12, 13, 14)
        if (cubes[1] <= availableCubes[0] && cubes[2] <= availableCubes[1] && cubes[3] <= availableCubes[2]) {
            return cubes[0]
        }
        return 0
    }

    fun getMinimumSetOfCubes(line: String): IntArray {
        val parts = """(: |; )""".toRegex().split(line)
            .flatMap { p -> """\s""".toRegex().split(p.replace(",", "")) }
        return minimumSetOfCubes(parts)
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
