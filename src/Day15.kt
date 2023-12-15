fun main() { // --- Day 15: Lens Library ---

    fun parse(input: List<String>) = input.single().splitToSequence(",")

    fun hash(s: String) = s.fold(0) { currentValue, char -> ((char.code + currentValue) * 17) % 256 }

    fun getBoxes(input: List<String>): MutableMap<Int, MutableMap<String, Int>> {
        val boxes = mutableMapOf<Int, MutableMap<String, Int>>()
        parse(input).forEach { step ->
            val (label: String, op: String, focalLength: String) = """(\w+)([=|-])(\d?)""".toRegex()
                .find(step)?.destructured ?: throw RuntimeException("Unexpected step: $step")
            val box = hash(label)
            when (op) {
                "=" -> boxes.getOrPut(box) { mutableMapOf() }[label] = focalLength.toInt()
                "-" -> boxes[box]?.remove(label)
                else -> throw RuntimeException("Unexpected operation: $op")
            }
        }
        return boxes
    }

    fun part1(input: List<String>) = parse(input).sumOf { hash(it) }

    fun part2(input: List<String>) = getBoxes(input).map { (box, slots) ->
        slots.toList().mapIndexed { slot, (_, focalLength) -> (1 + box) * (slot + 1) * focalLength }
    }.flatten().sum()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}
