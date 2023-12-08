fun main() { // --- Day 8: Haunted Wasteland ---
    fun parse(input: String): Pair<Iterator<Char>, Map<String, Pair<String, String>>> {
        val parts = input.split("\n\n")
        val steps = parts[0].asSequence().repeat().iterator()
        val map = parts[1].split("\n").associate { l ->
            val m = Regex("""^(\w+) = \((\w+), (\w+)\)$""").matchEntire(l) ?: throw Error("Incorrect input: $l")
            m.groupValues[1] to Pair(m.groupValues[2], m.groupValues[3])
        }
        return Pair(steps, map)
    }

    fun getDistanceToGoal(startPosition: String, steps: Iterator<Char>, map: Map<String, Pair<String, String>>): Int {
        var counter = 0
        var pos = startPosition
        while (!pos.endsWith('Z')) {
            val p = map[pos] ?: throw Error("Unknown position: $pos")
            pos = when (val step = steps.next()) {
                'L' -> p.first
                'R' -> p.second
                else -> throw Error("Unknown step: $step")
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
            .map { startPosition -> getDistanceToGoal(startPosition, steps, map).toLong() }
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
