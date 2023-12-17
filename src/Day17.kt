typealias HeatLossMap = Array<IntArray>

fun main() { // --- Day 17: Clumsy Crucible ---

    fun HeatLossMap.println() = this.forEach { println(it.contentToString()) }

    fun getNeighbors(r: Int, c: Int, h: Int, w: Int): Set<Pair<Int, Int>> {
        val rows = arrayOf(r - 1, r, r + 1)
        val cols = arrayOf(c - 1, c, c + 1)
        val neighbors = mutableSetOf<Pair<Int, Int>>()
        for (row in rows) {
            for (col in cols) {
                if (row in 0..<h && col in 0..<w)
                    neighbors.add(Pair(row, col))
            }
        }
        return neighbors
    }

    fun heatLossMap(startingPositions: Set<Pair<Int, Int>>, grid: Array<IntArray>): Array<IntArray> {
        val h = grid.size
        val w = grid[0].size
        val heatLossMap = Array(h) { IntArray(w) { Int.MAX_VALUE } }
        val closed = Array(h) { BooleanArray(w) { false } }

        // first: add starting positions
        for ((r, c) in startingPositions) {
            closed[r][c] = true
            grid[r][c] = 0
            heatLossMap[r][c] = grid[r][c]
        }

        // second: add next positions
        var toVisit = mutableSetOf<Pair<Int, Int>>()
        for ((sr, sc) in startingPositions) {
            val nb = getNeighbors(sr, sc, h, w).filter { (i, j) -> !closed[i][j] }
            toVisit.addAll(nb)
            for ((r, c) in nb) closed[r][c] = true
        }

        // third: Iterate filling in toVisit array deque
        val limit = h * w
        var counter = 0
        while (toVisit.isNotEmpty() && counter++ < limit) {
            val nextToVisit = mutableSetOf<Pair<Int, Int>>()
            for ((i, j) in toVisit) {
                val neighbors = getNeighbors(i, j, h, w)
                heatLossMap[i][j] = grid[i][j] + (neighbors.minOf { (nr, nc) -> heatLossMap[nr][nc] }) // !! not minOf
                nextToVisit += neighbors.filter { (i, j) -> !closed[i][j] }
                for ((r, c) in neighbors) closed[r][c] = true
            }
            toVisit = nextToVisit
        }

        println("Finished in $counter iterations.")

        // fourth: flood remaining positions, if any
        for ((i, j) in toVisit) {
            val neighbors = getNeighbors(i, j, h, w)
            heatLossMap[i][j] = grid[i][j] + (neighbors.minOf { (nr, nc) -> heatLossMap[nr][nc] })
            for ((r, c) in neighbors) closed[r][c] = true
        }

        return heatLossMap
    }

    fun part1(input: List<String>) =
        (heatLossMap(
            setOf(Pair(0, 0)),
            input.map { it.toIntArray() }.toTypedArray()
        ).also { it.println() })[0][0]

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
//    check(part2(testInput) == 1)
//
//    val input = readInput("Day17")
//    part1(input).println()
//    part2(input).println()
}
