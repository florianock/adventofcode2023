fun main() {
    fun hash(s: String): Int =
        s.fold(0) { currentValue, char -> ((char.code + currentValue) * 17) % 256 }

    fun part1(input: List<String>): Int = input.single().splitToSequence(",").map { hash(it) }.sum()

    fun part2(input: List<String>): Int {
        val boxes: MutableMap<Int, MutableMap<String, Int>> = mutableMapOf()
        input.single().splitToSequence(",").forEach { step ->
            val (label: String, op: String, focalLength: String) = """(\w+)([=|-])(\d?)""".toRegex()
                .find(step)?.destructured ?: throw RuntimeException("Unexpected step: $step")
            val box = hash(label)
            when (op) {
                "=" -> {
                    val newBoxMap = boxes.getOrDefault(box, mutableMapOf())
                    newBoxMap[label] = focalLength.toInt()
                    boxes[box] = newBoxMap
                }

                "-" -> boxes[box]?.remove(label)
                else -> throw RuntimeException("Unexpected operation: $op")
            }
        }
        return boxes.also(::println).map { (box, slots) ->
            slots.toList()
                .mapIndexed { slot, (_, focalLength) -> (1 + box) * (slot + 1) * focalLength }
        }
            .flatten().sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}
