enum class Direction { North, East, South, West }

fun main() { // --- Day 10: Pipe Maze ---
    val visuals = true

    // verticaal: 7F; 7|; |F; J|; |L; JF; JL; 7L
    // horizontaal: J; L; J; L; -; L; J; -
    //              -; -; 7; 7; 7; F; F; F
    val definitions = hashMapOf(
        Direction.North to arrayOf('7', '|', 'F'),
        Direction.East to arrayOf('J', '-', '7'),
        Direction.South to arrayOf('L', '|', 'J'),
        Direction.West to arrayOf('F', '-', 'L')
    )

    fun getNextPosition(pos: Pair<Int, Int>, step: Char, heading: Direction): Pair<Pair<Int, Int>, Direction>? {
        return when (heading) {
            Direction.North ->
                when (step) {
                    '7' -> Pair(Pair(pos.first, pos.second - 1), Direction.West)    // heading - 1  West
                    '|' -> Pair(Pair(pos.first - 1, pos.second), heading)           // continue     North
                    'F' -> Pair(Pair(pos.first, pos.second + 1), Direction.East)    // heading + 1  East
                    else -> null
                }

            Direction.East ->
                when (step) {
                    'J' -> Pair(Pair(pos.first - 1, pos.second), Direction.North)   // heading - 1  North
                    '-' -> Pair(Pair(pos.first, pos.second + 1), heading)           // continue     East
                    '7' -> Pair(Pair(pos.first + 1, pos.second), Direction.South)   // heading + 1  South
                    else -> null
                }

            Direction.South ->
                when (step) {
                    'J' -> Pair(Pair(pos.first, pos.second - 1), Direction.West)    // heading + 1  West
                    '|' -> Pair(Pair(pos.first + 1, pos.second), heading)           // continue     South
                    'L' -> Pair(Pair(pos.first, pos.second + 1), Direction.East)    // heading - 1  East
                    else -> null
                }

            Direction.West ->
                when (step) {
                    'L' -> Pair(Pair(pos.first - 1, pos.second), Direction.North)   // heading + 1  North
                    '-' -> Pair(Pair(pos.first, pos.second - 1), heading)           // continue     West
                    'F' -> Pair(Pair(pos.first + 1, pos.second), Direction.South)   // heading - 1  South
                    else -> null
                }
        }
    }

    fun getGridAndStartPosition(input: List<String>): Pair<Array<Array<Char>>, Pair<Int, Int>> {
        val startRow = input.indexOfFirst { l -> l.contains('S') }
        val startCol = input[startRow].indexOf('S')
        val grid = Array(input.size) { i -> Array(input[i].length) { j -> input[i][j] } }
        return Pair(grid, Pair(startRow, startCol))
    }

    fun loopAround(start: Pair<Int, Int>, heading: Direction, input: List<String>): Array<Pair<Int, Int>>? {
        val nextPos = when (heading) {
            Direction.North -> Pair(start.first - 1, start.second)
            Direction.East -> Pair(start.first, start.second + 1)
            Direction.South -> Pair(start.first + 1, start.second)
            Direction.West -> Pair(start.first, start.second - 1)
        }

        var steps = arrayOf(start, nextPos)
        var pos = nextPos
        var currentHeading = heading
        while (pos != start) {
            val next = getNextPosition(pos, input[pos.first][pos.second], currentHeading) ?: return null
            steps += next.first
            pos = next.first
            currentHeading = next.second
        }
        return steps
    }

    fun findLoop(start: Pair<Int, Int>, input: List<String>): Array<Pair<Int, Int>> {
        var loop = emptyArray<Pair<Int, Int>>()
        for (heading in Direction.entries) {
            loop = loopAround(start, heading, input) ?: continue
            break
        }
        return loop
    }

    fun part1(input: List<String>): Int {
        val (grid, start) = getGridAndStartPosition(input)
        val loop = findLoop(start, input).toSet()
        if (visuals) print2d(grid, mapOf(Colors.Yellow to loop))
        return loop.size / 2
    }

    fun findUnflooded(gridWithHeight: Array<Array<Pair<Char, Int?>>>): Pair<Int, Int>? {
        val startRow = gridWithHeight.indexOfFirst { r -> r.any { c -> c.second == null } }
        if (startRow < 0) return null
        val startCol = gridWithHeight[startRow].indexOfFirst { c -> c.second == null }
        return Pair(startRow, startCol)
    }

    fun part2(input: List<String>): Int {
        val (grid, start) = getGridAndStartPosition(input)
        val loop = findLoop(start, input).toSet()
        val gridWithHeight = grid.mapIndexed { i, row ->
            row.mapIndexed { j, cell ->
                val height = if (loop.contains(Pair(i, j))) Int.MAX_VALUE else null
                Pair(cell, height)
            }.toTypedArray()
        }.toTypedArray()

        var height = 1
        while (true) {
            val (startRow, startCol) = findUnflooded(gridWithHeight) ?: break
            gridWithHeight.flood(Pair(startRow, startCol), height)
            height++
        }

        val loopRows = loop.minOf { (r, _) -> r }..loop.maxOf { (r, _) -> r }
        val loopCols = loop.minOf { (_, c) -> c }..loop.maxOf { (_, c) -> c }

        val outsides = mutableSetOf<Int>()
        var boxedIn = mutableSetOf<Pair<Int, Int>>()
        for (r in gridWithHeight.indices) {
            for (c in gridWithHeight[r].indices) {
                if (gridWithHeight[r][c].second != null && gridWithHeight[r][c].second!! < Int.MAX_VALUE) {
                    if (loopRows.contains(r) && loopCols.contains(c)) {
                        boxedIn.add(Pair(r, c))
                    } else {
                        outsides.add(gridWithHeight[r][c].second!!)
                    }
                }
            }
        }
        boxedIn = boxedIn.filter { (i, j) -> !outsides.contains(gridWithHeight[i][j].second) }.toMutableSet()
        if (visuals) print2d(
            gridWithHeight.map { r -> r.map { c -> c.first }.toTypedArray() }.toTypedArray(),
            mapOf(Colors.Yellow to loop, Colors.Green to boxedIn)
        )
        // slide: zien of groepen verbonden zijn?
        return boxedIn.also(::println).size
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day10_test1")
    check(part1(testInput1) == 4)
    val testInput2 = readInput("Day10_test2")
    check(part1(testInput2) == 8)
    val input = readInput("Day10")
    part1(input).println()

//    val testInput3 = readInput("Day10_test3")
//    check(part2(testInput3) == 4)
//    val testInput4 = readInput("Day10_test4")
//    check(part2(testInput4) == 4)
//    val testInput5 = readInput("Day10_test5")
//    check(part2(testInput5) == 8)
//    val testInput6 = readInput("Day10_test6")
//    check(part2(testInput6) == 10)
    part2(input).println() // 794 too high
}
