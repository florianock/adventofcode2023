fun main() { // --- Day 14: Parabolic Reflector Dish ---

    fun countLoad(input: List<String>): Int {
        return input.foldIndexed(0) { inverseLoad, acc, row ->
            val load = input.size - inverseLoad
            var result = acc
            for (c in row) {
                if (c == 'O') result += load
            }
            result
        }
    }

    fun tiltNorthAndCountLoad(input: List<String>): Int {
        val counter = Array(input[0].length) { Pair(0, 0) }
        return input.foldIndexed(counter) { inverseLoad, acc, row ->
            val load = input.size - inverseLoad
            acc.mapIndexed { i, cc ->
                when (row[i]) {
                    '.' -> cc.copy(second = cc.second + 1)
                    '#' -> cc.copy(second = 0)
                    'O' -> cc.copy(first = cc.first + load + cc.second)
                    else -> throw RuntimeException("Unknown character: ${row[i]}")
                }
            }.toTypedArray()
        }.sumOf { it.first }
    }

    fun part1(input: List<String>): Int = tiltNorthAndCountLoad(input)

    fun cycle(input: List<String>): List<String> {
        // N
        var rocks = arrayOf<Pair<Int, Int>>()
        val northed = input.also(::println).mapIndexed { r, row ->
            var dotsCount = 0
            row.mapIndexed { c, char ->
                when (char) {
                    '.' -> {
                        dotsCount++
                        '.'
                    }

                    '#' -> {
                        dotsCount = 0
                        '#'
                    }

                    'O' -> {
                        rocks += Pair(r, c - dotsCount)
                        '.'
                    }

                    else -> throw RuntimeException("Unknown character: $char")
                }
            }.toTypedArray()
        }.toTypedArray()

        for ((row, col) in rocks) {
            northed[row][col] = 'O'
        }

        val flo = northed.map { it.joinToString("") }.toList().also(::println)

        // W
        // S
        // E
        return input
    }

    fun countLoadAfterCycles(input: List<String>, n: Int): Int {
        val states = hashMapOf(input to Pair(0, countLoad(input)))
        var cycles = 1
        while (cycles < n) {
            val newState = cycle(input)
            if (states.containsKey(newState)) break
            states[newState] = Pair(cycles, countLoad(newState))
            cycles++
        }
        val result = n % cycles
        return states.values.find { it.first == result }?.second
            ?: throw RuntimeException("No value found for state at cycle $result")
    }

    fun part2(input: List<String>): Int = countLoadAfterCycles(input, 1_000_000_000).also(::println)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    part1(input).println() // 106186
//    part2(input).println()
}
