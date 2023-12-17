typealias Pattern = Array<CharArray>

fun main() { // --- Day 13: Point of Incidence ---

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

    fun rowsReflect(a: CharArray, b: CharArray, isPart2: Boolean): Int {
        return a.mapIndexed { i, c ->
            if (c != b[i]) 1
            else 0
        }.sum()
    }

    fun Pattern.isReflection(lowerLine: Int, isPart2: Boolean): Boolean {
        if (lowerLine < 0) return false
        val upperLine = lowerLine + 1
        val checkRange =
            if ((this.size - upperLine) <= lowerLine)
                if (lowerLine == this.size - 2) 0..0
                else 0..<(this.size - upperLine)
            else
                if (lowerLine == 0) 0..0
                else 0..lowerLine

        var errors = 0
        for (i in checkRange) {
            errors += rowsReflect(this[lowerLine - i], this[upperLine + i], isPart2)
            if (errors > 1) return false
        }

        return if (isPart2) errors == 1 else errors == 0
    }

    fun Pattern.countRowsAboveFold(isPart2: Boolean): Int? {
        for (i in this.indices) {
            if (i == this.size - 1) break
            if (this.isReflection(i, isPart2)) return (i + 1)
        }
        return null
    }

    fun Pattern.countColumnsLeftOfFold(isPart2: Boolean) = this.transpose().countRowsAboveFold(isPart2)

    fun analyseReflections(input: List<String>, isPart2: Boolean) =
        getPatterns(input).fold(0) { acc, p ->
            acc + (p.countColumnsLeftOfFold(isPart2)
                ?: (p.countRowsAboveFold(isPart2)?.times(100)
                    ?: throw RuntimeException("No reflection in pattern ${p.map { it.joinToString("") }}")))
        }

    fun part1(input: List<String>) = analyseReflections(input, false)

    fun part2(input: List<String>) = analyseReflections(input, true)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

//    val testInput2 = readInput("Day13_pattern91")
//    check(part1(testInput2) == 300)
//    check(part2(testInput2) == 1400)

//    val testInput3 = readInput("Day13_pattern62")
//    check(part1(testInput3) == 400)
//    check(part2(testInput3) == 1)

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
