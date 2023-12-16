fun main() { // --- Day 8: Haunted Wasteland ---

    fun parse(input: String): Pair<Iterator<Char>, Map<String, Pair<String, String>>> {
        val parts = input.split("\n\n")
        val steps = parts[0].asSequence().repeat().iterator()
        val map = parts[1].split("\n").associate { l ->
            val (pos, left, right) = Regex("""^(\w+) = \((\w+), (\w+)\)$""").matchEntire(l)?.destructured
                ?: throw RuntimeException("Invalid instruction: $l")
            pos to Pair(left, right)
        }
        return Pair(steps, map)
    }

    fun getDistanceToGoal(startPosition: String, steps: Iterator<Char>, map: Map<String, Pair<String, String>>): Int {
        var counter = 0
        var pos = startPosition
        while (!pos.endsWith('Z')) {
            val p = map[pos] ?: throw RuntimeException("Unknown position: $pos")
            pos = when (val step = steps.next()) {
                'L' -> p.first
                'R' -> p.second
                else -> throw RuntimeException("Unknown step: $step")
            }
            counter++
        }
        return counter
    }

    fun part1(input: String): Int {
        val (steps, map) = parse(input)
        return getDistanceToGoal("AAA", steps, map)
    }

    fun part2(input: String): Long {
        val (steps, map) = parse(input)
        return map.keys
            .filter { it.endsWith('A') }
            .map { getDistanceToGoal(it, steps, map).toLong() }
            .leastCommonMultiple()
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInputText("Day08_test1")
    check(part1(testInput1) == 6)
    val testInput2 = readInputText("Day08_test2")
    check(part2(testInput2) == 6L)

    val input = readInputText("Day08")
    part1(input).println()
    part2(input).println()
}
