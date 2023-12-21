fun main() { // --- Day 21: Step Counter ---

    fun parse(input: List<String>): Pair<Pair<Int, Int>, Array<BooleanArray>> {
        var start = Pair(0, 0)
        val farm =
            Array(input.size) { r ->
                BooleanArray(input[0].length) { c ->
                    if (input[r][c] == 'S') start = Pair(r, c)
                    input[r][c] != '#'
                }
            }
        return Pair(start, farm)
    }

    fun getNeighbors(p: Pair<Int, Int>, farm: Array<BooleanArray>, isInfinite: Boolean = false): Set<Pair<Int, Int>> {
        val h = farm.size
        val w = farm[0].size
        val ns = neighbors(p, false)
        return if (!isInfinite)
            ns.filter { (r, c) -> r in 0..<h && c in 0..<w && farm[r][c] }.toSet()
        else
            ns.filter { (r, c) ->
                val rMapped = if (r < 0) h + (r % h) else r
                val cMapped = if (c < 0) w + (c % w) else c
                farm[rMapped % h][cMapped % w]
            }.toSet()
    }

    fun part1(input: List<String>, steps: Int): Int {
        val (start, farm) = parse(input)
        return (0..<steps).fold(setOf(start)) { acc, _ ->
            acc.flatMap { p -> getNeighbors(p, farm) }.toSet()
        }.count()
    }

    fun part2(input: List<String>, steps: Int): Long {
        val (start, farm) = parse(input)
        return (0..<steps).fold(setOf(start)) { acc, _ ->
            acc.flatMap { p -> getNeighbors(p, farm, true) }.toSet()
        }.count().also(::println).toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput, 1) == 2)
    check(part1(testInput, 2) == 4)
    check(part1(testInput, 3) == 6)
    check(part1(testInput, 6) == 16)
    check(part2(testInput, 6) == 16L)
    check(part2(testInput, 10) == 50L)
    check(part2(testInput, 50) == 1_594L)
    check(part2(testInput, 100) == 6_536L)
    check(part2(testInput, 500) == 167_004L)
//    check(part2(testInput, 1_000) == 668_697L)
//    check(part2(testInput, 5_000) == 16_733_044L)

//    val input = readInput("Day21")
//    part1(input, 64).println()
//    part2(input, 26_501_365).println()
}
