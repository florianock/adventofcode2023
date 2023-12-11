import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText
import kotlin.math.abs
import kotlin.math.pow

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Reads whole text from the given input txt file.
 */
fun readInputText(name: String) = Path("src/$name.txt").readText()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

/**
 * Helper function for getting neighbors of a point in a 2-D matrix
 */
fun neighbors(p: Pair<Int, Int>): Set<Pair<Int, Int>> {
    return setOf(
        Pair(p.first - 1, p.second - 1), Pair(p.first - 1, p.second), Pair(p.first - 1, p.second + 1),
        Pair(p.first, p.second - 1), Pair(p.first, p.second + 1),
        Pair(p.first + 1, p.second - 1), Pair(p.first + 1, p.second), Pair(p.first + 1, p.second + 1)
    )
}

/**
 * Helper function for testing adjacency of two points in a 2-D matrix
 */
fun isAdjacent(p1: Pair<Int, Int>, p2: Pair<Int, Int>) = p1 in neighbors(p2)

enum class Colors { Red, Green, Yellow }

/**
 * Print 2d Array of Array<Char>
 */
fun print2d(matrix: Array<Array<Char>>, colors: Map<Colors, Set<Pair<Int, Int>>>?) {
    val m = Array(matrix.size) { r ->
        CharArray(matrix[r].size) { c ->
            matrix[r][c]
        }
    }
    print2d(m, colors)
}

/**
 * Print 2D matrix to console
 */
fun print2d(
    matrix: Array<CharArray>,
    colors: Map<Colors, Set<Pair<Int, Int>>>?
) {
    val red = "\u001b[31m"
    val green = "\u001b[32m"
    val yellow = "\u001b[33m"
    val reset = "\u001b[0m"
    for (r in matrix.indices) {
        val row = matrix[r]
        var line = ""
        for (c in row.indices) {
            val cell = row[c]
            if (colors != null) {
                if (colors.containsKey(Colors.Red) && Pair(r, c) in colors[Colors.Red]!!) {
                    line += red + cell + reset
                } else if (colors.containsKey(Colors.Yellow) && Pair(r, c) in colors[Colors.Yellow]!!) {
                    line += yellow + cell + reset
                } else if (colors.containsKey(Colors.Green) && Pair(
                        r,
                        c
                    ) in colors[Colors.Green]!!
                ) { // or part of adjacent number
                    line += green + cell + reset
                } else {
                    line += cell
                }
            } else {
                line += cell
            }
        }
        line.println()
    }
}

/**
 * Calculate the power of a number
 */
infix fun Number.toPowerOf(exponent: Number): Int = (this.toDouble().pow(exponent.toDouble())).toInt()

/**
 * Infinitely repeat a sequence.
 */
fun <T> Sequence<T>.repeat() = sequence { while (true) yieldAll(this@repeat) }

/**
 * Get the least common multiple for two Long numbers.
 */
fun leastCommonMultiple(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    val iterator = generateSequence(larger) {
        val next = it + larger
        if (next < maxLcm) next else null
    }.iterator()
    while (iterator.hasNext()) {
        val lcm = iterator.next()
        if (lcm % a == 0L && lcm % b == 0L) return lcm
    }
    return maxLcm
}

/**
 * Get the least common multiple for a list of Long numbers.
 */
fun List<Long>.leastCommonMultiple(): Long {
    return this.subList(1, this.size).fold(this[0]) { acc, n ->
        leastCommonMultiple(acc, n)
    }
}

/**
 * Flood Fill Algorithm in a Collection, starting at the given start position.
 */
fun <E> Array<Array<Pair<E, Int?>>>.flood(pos: Pair<Int, Int>, target: Int) {
    val maxRow = this.size - 1
    val maxCol = this[0].size - 1

    if (pos.first < 0 || pos.first > maxRow || pos.second < 0 || pos.second > maxCol)
        return

    if (this[pos.first][pos.second].second == Int.MAX_VALUE || this[pos.first][pos.second].second != null)
        return

    this[pos.first][pos.second] = Pair(this[pos.first][pos.second].first, target)

    this.flood(Pair(pos.first + 1, pos.second), target)
    this.flood(Pair(pos.first - 1, pos.second), target)
    this.flood(Pair(pos.first, pos.second + 1), target)
    this.flood(Pair(pos.first, pos.second - 1), target)
}

/**
 * Transpose a 2D CharArray.
 */
fun Array<CharArray>.transpose(): Array<CharArray> {
    val cols = this[0].size
    val rows = this.size
    return Array(cols) { j -> CharArray(rows) { i -> this[i][j] } }
}

/**
 * Get all pairs of a Collection.
 */
fun <T> Iterable<T>.allPairs(): Set<Pair<T, T>> {
    val unpaired = this.toMutableSet()
    return this.fold(mutableSetOf()) { acc, a ->
        unpaired.remove(a)
        for (b in unpaired) {
            acc.add(Pair(a, b))
        }
        acc
    }
}

/**
 * Create a Grid from a list of Strings.
 */
fun List<String>.toGrid(): Array<CharArray> {
    return Array(this.size) { r ->
        val row = this[r]
        CharArray(row.length) { c -> this[r][c] }
    }
}

/**
 * Get the Manhattan Distance of two points.
 */
fun manhattanDistance(a: Pair<Long, Long>, b: Pair<Long, Long>): Long =
    abs(a.first - b.first) + abs(a.second - b.second)
