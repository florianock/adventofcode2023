import kotlin.math.abs

typealias Pattern = Array<Int>

fun main() { // --- Day 13: Point of Incidence ---

    fun toBinary(line: String): Int =
        line.replace('.', '0').replace('#', '1').reversed().toInt(2)

    fun getPatterns(input: List<String>): Array<Pattern> {
        var patterns = arrayOf<Pattern>()
        var pattern = arrayOf<Int>()
        for (l in input) {
            if (l == "") {
                patterns += pattern
                pattern = arrayOf()
            } else {
                pattern += toBinary(l)
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

    fun Pattern.print(pattern: Pattern) {
        println(pattern.map { it.toString(2) })
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

//        println("($lowerLine, ${this.size}): $checkRange")
        var fixedSmudge = false
        for (i in checkRange) {
            val result = rowsReflect(this[lowerLine - i], this[upperLine + i], isPart2)
            if (!fixedSmudge) fixedSmudge = result.second
            if (!result.first) return false
        }

//        if (!fixSmudge || fixedSmudge) println("found! ${lowerCut + 1} rows above fold\n")
        return if (isPart2) fixedSmudge else true
    }

    fun Pattern.countRowsAboveFold(isPart2: Boolean): Int? {
        for (i in this.indices) {
            if (i == this.size - 1) return null
            if (this.isReflection(i, isPart2)) {
                return i + 1
            }
        }
        return null
    }

    fun Pattern.countColumnsLeftOfFold(isPart2: Boolean): Int? {
//        println("flipping..")
        val tpattern = this.map { it.toString(2).toCharArray() }.toTypedArray().transpose().reversed()
        return tpattern.map {
            it.joinToString("").toInt(2)
        }.toTypedArray().countRowsAboveFold(isPart2)
    }

    fun analyseReflections(input: List<String>, isPart2: Boolean): Int {
        return getPatterns(input)
            .sumOf { (it.countRowsAboveFold(isPart2)?.times(100)) ?: it.countColumnsLeftOfFold(isPart2)!! }
//            .also(::println)
//            .sum()
    }

    fun part1(input: List<String>): Int {
        return analyseReflections(input, false)
    }

    fun part2(input: List<String>): Int {
        return analyseReflections(input, true)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

    val input = readInput("Day13")
    part1(input).println() // 31265
    part2(input).println() // 38073 too low; 39458 too high
}
