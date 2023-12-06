import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText
import kotlin.math.pow

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

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
        Pair(p.first-1, p.second-1), Pair(p.first-1, p.second), Pair(p.first-1, p.second+1),
        Pair(p.first, p.second-1),                              Pair(p.first, p.second+1),
        Pair(p.first+1, p.second-1), Pair(p.first+1, p.second), Pair(p.first+1, p.second+1)
    )
}

/**
 * Helper function for testing adjacency of two points in a 2-D matrix
 */
fun isAdjacent(p1: Pair<Int, Int>, p2: Pair<Int, Int>) = p1 in neighbors(p2)

enum class Colors { Red, Green, Yellow }

/**
 * Print 2D matrix to console
 */
fun print2d(
    matrix: Array<Array<Char>>,
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
                if (colors.containsKey(Colors.Red) && Pair(r,c) in colors[Colors.Red]!!) {
                    line += red + cell + reset
                } else if (colors.containsKey(Colors.Yellow) && Pair(r,c) in colors[Colors.Yellow]!!) {
                    line += yellow + cell + reset
                } else  if (colors.containsKey(Colors.Green) && Pair(r,c) in colors[Colors.Green]!!) { // or part of adjacent number
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
