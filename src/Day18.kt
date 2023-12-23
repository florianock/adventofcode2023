import kotlin.math.abs

fun main() { // --- Day 18: Lavaduct Lagoon ---
    val visuals = true

    fun toDirection(s: String): Direction {
        return when (s) {
            "U" -> Direction.North
            "R" -> Direction.East
            "D" -> Direction.South
            "L" -> Direction.West
            else -> throw RuntimeException("Invalid direction: $s")
        }
    }

    fun getPositions(instructions: List<Triple<Direction, Int, String>>): Pair<Array<Pair<Int, Int>>, Pair<Int, Int>> {
        val positions = instructions.scan(Pair(0, 0)) { (r, c), i ->
            when (i.first) {
                Direction.North -> Pair(r - i.second, c)
                Direction.East -> Pair(r, c + i.second)
                Direction.South -> Pair(r + i.second, c)
                Direction.West -> Pair(r, c - i.second)
            }
        }.toSet()

        val splitPositions = positions.unzip()
        val (maxRow, maxCol) = Pair(splitPositions.first.max(), splitPositions.second.max())
        val (minRow, minCol) = Pair(splitPositions.first.min(), splitPositions.second.min())

        val width = maxCol + abs(minCol) + 1
        val height = maxRow + abs(minRow) + 1

        println("Found grid dimensions: $width x $height = max. ${width * height} possible lagoon size.")

        // Shift to have (0, 0) as minimum
        val newRows = if (minRow < 0) {
            println("Shifted rows!")
            val toAdd = abs(minRow)
            splitPositions.first.map { it + toAdd }
        } else {
            splitPositions.first
        }
        val newCols = if (minCol < 0) {
            println("Shifted cols!")
            val toAdd = abs(minCol)
            splitPositions.second.map { it + toAdd }
        } else {
            splitPositions.second
        }

        return Pair(newRows.zip(newCols).toTypedArray(), Pair(width, height))
    }

    fun getWalls(positions: Array<Pair<Int, Int>>): Set<Pair<Int, Int>> {
        val walls = mutableSetOf<Pair<Int, Int>>()
        (positions + positions.first()).toList().windowed(2) { lst ->
            val a = lst[0]
            val b = lst[1]
            if (a.second == b.second) { // move row
                if (a.first < b.first)
                    for (r in a.first..<b.first) walls.add(Pair(r, b.second))
                else
                    for (r in b.first..<a.first) walls.add(Pair(r, b.second))
            } else { // move col
                if (a.second < b.second)
                    for (c in a.second..<b.second) walls.add(Pair(b.first, c))
                else
                    for (c in b.second..<a.second) walls.add(Pair(b.first, c))
            }
            walls.addAll(lst.toSet())
        }
        return walls
    }

    fun part1(input: List<String>): Int {
        val instructions =
            input.map { it.split(" ") }.map { Triple(toDirection(it[0]), it[1].toInt(), it[2]) }

        val (positions, dimensions) = getPositions(instructions)
        val walls = getWalls(positions)

        // build grid
        val (width, height) = dimensions
        val grid = Array(height) { BooleanArray(width) { false } }
        walls.forEach { (r, c) -> grid[r][c] = true }

        // get volumes
        val volumes = IntArray(height) { 0 }
        var isOpen = false
        var openedUp: Boolean? = null
        var openedAt = -1

        for (i in 0..<height) {
            isOpen = false
            openedUp = null
            openedAt = -1
            for (j in 0..<width) {
                if (!grid[i][j]) continue
                if (j - 1 in 0..<width && grid[i][j - 1]) { // line of trenches
                    if (openedUp != null) {
                        if (grid[i][j] && i - 1 in 0..<height && grid[i - 1][j] && openedUp ||
                            grid[i][j] && i + 1 in 0..<height && grid[i + 1][j] && !openedUp
                        ) { // endPoint
                            if (isOpen) volumes[i] += 1
                            isOpen = !isOpen
                            openedUp = null
                        } else {
                            if (isOpen) volumes[i] += 1
                            openedAt = j
                        }
                    } else {
                        volumes[i] += 1
                    }
                } else {
                    if (!isOpen) {
                        volumes[i] += 1
                        openedAt = j
                        if (i - 1 in 0..<height && grid[i - 1][j]) openedUp = true
                        else if (i + 1 in 0..<height && grid[i + 1][j]) openedUp = false
                    } else {
                        volumes[i] += j - openedAt
                        openedAt = -1
                        openedUp = null
                    }
                    isOpen = !isOpen
                }
            }
        }

        if (visuals) {
            for (i in 0..<height) {
                val row = CharArray(width) { '.' }
                for (j in 0..<width) {
                    if (walls.contains(Pair(i, j))) row[j] = '#'
                }
//                row.joinToString("").println()
                "${"%-3d".format(volumes[i])} |> ${row.joinToString("")} <| ${volumes[i]}".println()
            }
        }

        return volumes.sum()

//        val volumes =
//            walls.sortedWith(compareBy({ it.first }, { it.second })).groupingBy { it.first }
//                .aggregate { _, acc: Triple<Int, Pair<Boolean?, Boolean>, Pair<Int, Int>>?, current, _ ->
//                    if (acc == null) // first
//                        if (walls.contains(Pair(current.first - 1, current.second)))
//                            Triple(1, Pair(true, true), current)
//                        else if (walls.contains(Pair(current.first + 1, current.second)))
//                            Triple(1, Pair(false, true), current)
//                        else
//                            Triple(1, Pair(null, true), current)
//                    else {
//                        val counter = acc.first
//                        val openedUp = acc.second.first
//                        val insideVolume = acc.second.second
//                        val previous = acc.third
//                        if (isAdjacent(previous, current)) {
////                            if (endPoint) Triple(counter + 1, !insideVolume, current)
//                            Triple(counter + 1, insideVolume, current)
//                        } else {
//                            val updatedCounter =
//                                if (insideVolume) counter + current.second - previous.second else counter
//                            Triple(updatedCounter, !insideVolume, current)
//                        }
//                    }
//                }.values.map { it.first }

//        for (i in 0..<height) {
//            val row = CharArray(width) { '.' }
//            for (j in 0..<width) {
//                if (walls.contains(Pair(i, j))) row[j] = '#'
//            }
//            row.joinToString("").println()
//                "${"%-3d".format(volumes[i])} |> ${row.joinToString("")} <| ${volumes[i]}".println()
//        }
//        if (visuals) {
//        }

        // positions defines the shape of the lagoon.
        // Calculate flood size, or use floodfill
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62)
//    check(part2(testInput) == 1)

    val input = readInput("Day18")
    part1(input).println() // 34749, 35458, 36678 too low
//    part2(input).println()
}
