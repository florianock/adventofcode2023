import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun main() {
    val visitedMemorySize = 15_000
    val previousVisitedSizes = IntArray(visitedMemorySize) { -1 }

    fun getNextPositions(next: Pair<Int, Int>, symbol: Char, heading: Direction): Set<Pair<Pair<Int, Int>, Direction>> =
        if (heading.isVertical()) {
            when (symbol) {
                '\\' -> setOf(
                    Pair(
                        Pair(next.first, next.second + (if (heading == Direction.South) 1 else -1)),
                        if (heading == Direction.North) Direction.West else Direction.East
                    )
                )

                '/' -> setOf(
                    Pair(
                        Pair(next.first, next.second + (if (heading == Direction.North) 1 else -1)),
                        if (heading == Direction.North) Direction.East else Direction.West
                    )
                )

                '-' -> setOf(getNextPositions(next, '/', heading), getNextPositions(next, '\\', heading))
                    .flatten().toSet()

                else -> setOf(
                    Pair(
                        Pair(next.first + (if (heading == Direction.South) 1 else -1), next.second),
                        heading
                    )
                )
            }
        } else {
            when (symbol) {
                '\\' -> setOf(
                    Pair(
                        Pair(next.first + (if (heading == Direction.East) 1 else -1), next.second),
                        if (heading == Direction.East) Direction.South else Direction.North
                    )
                )

                '/' -> setOf(
                    Pair(
                        Pair(next.first + (if (heading == Direction.West) 1 else -1), next.second),
                        if (heading == Direction.East) Direction.North else Direction.South
                    )
                )

                '|' -> setOf(getNextPositions(next, '/', heading), getNextPositions(next, '\\', heading))
                    .flatten().toSet()

                else -> setOf(
                    Pair(
                        Pair(next.first, next.second + (if (heading == Direction.East) 1 else -1)),
                        heading
                    )
                )
            }
        }

    fun completePath(startPosition: Pair<Pair<Int, Int>, Direction>, grid: Array<CharArray>)
            : Pair<Set<Pair<Pair<Int, Int>, Direction>>, Set<Pair<Int, Int>>> {
        val toVisit = mutableSetOf(startPosition)
        val visited = mutableSetOf<Pair<Int, Int>>()
        var previousCounter = 0
        var steps = 0
        val memory = previousVisitedSizes.clone()
        while (memory.any { it != visited.size } && toVisit.isNotEmpty()) {
            memory[previousCounter++] = visited.size
            if (previousCounter == visitedMemorySize) previousCounter = 0
            val (next, heading) = toVisit.first().also { toVisit.remove(it) }
            val result = getNextPositions(next, grid[next.first][next.second], heading)
            visited.add(next)
            val newPoints = result.filter {
                0 <= it.first.first && it.first.first < grid.size &&
                        0 <= it.first.second && it.first.second < grid[0].size
            }.toSet()
            toVisit += newPoints
            steps++
        }
        return Pair(toVisit, visited)
    }

    fun part1(input: List<String>): Int {
        val grid = Array(input.size) { i -> CharArray(input[i].length) { j -> input[i][j] } }
        val (_, energized) = completePath(Pair(Pair(0, 0), Direction.East), grid)
        grid.forEachIndexed { r, row ->
            println(row.mapIndexed { c, _ ->
                if (energized.contains(Pair(r, c))) '#'
                else '.'
            }.joinToString(""))
        }
        return energized.size
    }

    suspend fun part2(input: List<String>): Int {
        val grid = Array(input.size) { i -> CharArray(input[i].length) { j -> input[i][j] } }
        val numRows = input.size
        val numCols = input[0].length

        val starts: MutableSet<Pair<Pair<Int, Int>, Direction>> = mutableSetOf()
        (0..<numRows).forEach { i ->
            starts.add(Pair(Pair(i, 0), Direction.East))
            starts.add(Pair(Pair(i, numCols - 1), Direction.West))
        }
        (0..<numCols).forEach { j ->
            starts.add(Pair(Pair(0, j), Direction.South))
            starts.add(Pair(Pair(numRows - 1, j), Direction.North))
        }

        println("Found ${starts.size} start points...")
        return coroutineScope {
            starts.map { startPoint ->
                async {
                    val (_, energized) = completePath(startPoint, grid)
                    energized.size
                }
            }.awaitAll()
        }.max()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 46)
    check(part2(testInput) == 51)

    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}
