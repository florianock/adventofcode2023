fun main() { // --- Day 12: Hot Springs ---

    fun getParts(line: String): Pair<String, List<Int>> {
        val parts = line.split(" ")
        val map = parts[0]
        val groups = parts[1].split(",").map { n -> n.toInt() }
        return Pair(map, groups)
    }

    fun getRegex(groups: List<Int>): Regex {
        var regex = """^\.*#{${groups[0]}}"""
        for (n in 1..<groups.size) {
            regex += """\.+#{${groups[n]}}"""
        }
        regex += """\.*$"""
        return regex.toRegex()
    }

    fun isValid(line: String): Boolean {
        val (map, groups) = getParts(line)
        val regex = getRegex(groups)
        return regex.matches(map)
    }

    fun permutations(input: String): Set<String> {
        if (input.indexOf('?') == -1) return setOf(input)
        return setOf(
            permutations(input.replaceFirst('?', '.')),
            permutations(input.replaceFirst('?', '#'))
        ).flatten().toSet()
    }

    fun countSolutions(line: String): Int {
        val (map, groups) = getParts(line)
        return permutations(map).map { s -> getRegex(groups).matches(s) }.count { it }
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { l -> countSolutions(l) }
    }

    fun expand(input: List<String>): List<String> {
        return input.map { line ->
            val parts = line.split(" ")
            val copyRange = (0..<4)
            "${copyRange.fold(parts[0]) { acc, _ -> "$acc?${parts[0]}" }} " +
                    copyRange.fold(parts[1]) { acc, _ -> "$acc,${parts[1]}" }
        }
    }

    fun part2(input: List<String>): Int {
        val newInputs = expand(input).also(::println)
        return newInputs.sumOf { l -> countSolutions(l) }
    }

    // test if implementation meets criteria from the description, like:
    val validInput = readInput("Day12_valid")
    check(validInput.all { line -> isValid(line) })

    val testInput = readInput("Day12_test")
    println("1")
    check(part1(testInput) == 21)
//    println("2")
//    check(part2(testInput) == 525152)
    println("check done")

    val input = readInput("Day12")
    part1(input).println()
//    part2(input).println()
}
