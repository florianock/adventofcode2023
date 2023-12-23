import java.awt.geom.Line2D.linesIntersect
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() { // --- Day 22: Sand Slabs ---

    data class Brick(val id: Int, val front: Triple<Int, Int, Int>, val back: Triple<Int, Int, Int>) {
        val minX = min(this.front.first, this.back.first)
        val minY = min(this.front.second, this.back.second)
        val minZ = min(this.front.third, this.back.third)
        val maxX = max(this.front.first, this.back.first)
        val maxY = max(this.front.second, this.back.second)
        val maxZ = max(this.front.third, this.back.third)
        val width = abs(this.front.first - this.back.first)
        val depth = abs(this.front.second - this.back.second)
        val height = abs(this.front.third - this.back.third) + 1
        val changeDimension = when {
            front.first != back.first -> 'x'
            front.second != back.second -> 'y'
            front.third != back.third -> 'z'
            else -> 's' // single cube
        }

        fun supports(b: Brick): Boolean = this.maxZ == b.minZ - 1 &&
                linesIntersect(
                    this.front.first.toDouble(), this.front.second.toDouble(),
                    this.back.first.toDouble(), this.back.second.toDouble(),
                    b.front.first.toDouble(), b.front.second.toDouble(),
                    b.back.first.toDouble(), b.back.second.toDouble()
                )

        fun isSupportedBy(b: Brick) = b.supports(this)
        fun onGround() = this.minZ <= 1
    }

    fun parse(input: List<String>, ids: Iterator<Int>): List<Brick> {
        val bricks = input.map { line ->
            val (aX: String, aY: String, aZ: String, bX: String, bY: String, bZ: String) =
                """^(\d+),(\d+),(\d+)~(\d+),(\d+),(\d+)$""".toRegex()
                    .find(line)?.destructured ?: throw IllegalArgumentException("Invalid input line: $line")
            Brick(ids.next(), Triple(aX.toInt(), aY.toInt(), aZ.toInt()), Triple(bX.toInt(), bY.toInt(), bZ.toInt()))
        }
        return bricks
    }

    fun List<Brick>.fall(): List<Brick> {
        var updatedBricks = this
        while (true) {
            val newUpdatedBricks = updatedBricks.sortedBy { it.minZ }.map { brick ->
                if (!brick.onGround() && !updatedBricks.any { br -> br.supports(brick) }) {
                    brick.copy(
                        front = Triple(brick.front.first, brick.front.second, brick.front.third - 1),
                        back = Triple(brick.back.first, brick.back.second, brick.back.third - 1)
                    )
                } else {
                    brick
                }
            }
            if (newUpdatedBricks == updatedBricks) break
            else updatedBricks = newUpdatedBricks
        }
        return updatedBricks
    }

    fun part1(input: List<String>): Int {
        val bricks = parse(input, (0..input.size).iterator()).fall().onEach { l -> println(l) }
        return bricks.count { b ->
            val dependants = bricks.filter { br -> br.isSupportedBy(b) }
            dependants.isEmpty() || dependants.all { d -> bricks.any { other -> other != b && other.supports(d) } }
        }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    check(part1(testInput) == 5)
//    check(part2(testInput) == 1)

    val input = readInput("Day22")
    part1(input).println() // 495 too high
//    part2(input).println()
}
