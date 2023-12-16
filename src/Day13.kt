import kotlin.math.abs

typealias Pattern = Array<CharArray>

fun main() { // --- Day 13: Point of Incidence ---

    fun toBinary(line: String): Int =
        line.replace('.', '0').replace('#', '1').reversed().toInt(2)

    fun getPatterns(input: List<String>): Array<Pattern> {
        var patterns = arrayOf<Pattern>()
        var pattern = arrayOf<CharArray>()
        for (l in input) {
            if (l == "") {
                patterns += pattern
                pattern = arrayOf()
            } else {
                pattern += l.toCharArray()
            }
        }
        patterns += pattern
        return patterns
    }

    fun rowsReflect(a: Int, b: Int, isPart2: Boolean): Pair<Boolean, Boolean> {
        if (a - b == 0) return Pair(true, false)
        else if (isPart2) {
            val result = abs(a - b).toString(2).count { it == '1' } == 1
            return Pair(result, true)
        }
        return Pair(false, false)
    }

    fun isReflection(lowerLine: Int, binaryPattern: IntArray, isPart2: Boolean): Pair<Boolean, Int?> {
        if (lowerLine < 0) return Pair(false, 0)
        val upperLine = lowerLine + 1
        val checkRange =
            if ((binaryPattern.size - upperLine) <= lowerLine)
                if (lowerLine == binaryPattern.size - 2) 0..0
                else 0..<(binaryPattern.size - upperLine)
            else
                if (lowerLine == 0) 0..0
                else 0..lowerLine

        var fixedSmudge: Int? = null
        for (i in checkRange) {
            val result = rowsReflect(binaryPattern[lowerLine - i], binaryPattern[upperLine + i], isPart2)
            if (fixedSmudge == null && result.second) fixedSmudge = i
            if (!result.first) return Pair(false, null)
        }

        return if (isPart2) Pair(fixedSmudge != null, fixedSmudge) else Pair(true, null)
    }

    fun Pattern.countRowsAboveFold(idx: Int, isPart2: Boolean): Int? {
        val binaryPattern =
            this.map { toBinary(it.joinToString("")) }.toIntArray()
        for (i in this.indices) {
            if (i == this.size - 1) return null
            val (isReflection, _) = isReflection(i, binaryPattern, isPart2)
            if (isReflection) return (i + 1)
        }
        return null
    }

    fun Pattern.countColumnsLeftOfFold(i: Int, isPart2: Boolean) =
        if (i == 91) null
        else this.transpose().countRowsAboveFold(i, isPart2)

    fun analyseReflections(input: List<String>, isPart2: Boolean) =
        getPatterns(input).also { println("No. patterns: ${it.size}") }.runningFoldIndexed(0) { i, acc, p ->
            acc + (p.countColumnsLeftOfFold(i, isPart2).also { if (it != null) println("Pattern $i: $it") }
                ?: (p.countRowsAboveFold(i, isPart2)?.times(100).also { if (it != null) println("Pattern $i: $it") })
                ?: throw RuntimeException("No reflection in pattern ${p.contentToString()}"))
        }.also(::println).last()

    fun part1(input: List<String>) = analyseReflections(input, false)

    fun part2(input: List<String>) = analyseReflections(input, true)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

//    val testInput2 = readInput("Day13_test2")
//    check(part1(testInput2) == 300)  // colsfirst: 300; rowsfirst: 300
//    check(part2(testInput2) == 1400) // colsfirst: 1; rowsfirst: 1400

//    val testInput3 = readInput("Day13_test3")
//    check(part1(testInput3) == 400) // colsfirst: 400; rowsfirst: 400
//    check(part2(testInput3) == 1) // colsfirst: 1; rowsfirst: 100

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
