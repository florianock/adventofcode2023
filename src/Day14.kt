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

    fun tiltVertical(direction: Direction, input: List<String>): Set<Pair<Int, Int>> {
        val dotsCount = IntArray(input[0].length) { 0 }
        val (_, rocks) =
            when (direction) {
                Direction.North -> {
                    input.foldIndexed(
                        Pair(
                            dotsCount,
                            mutableSetOf<Pair<Int, Int>>()
                        )
                    ) { r, (count, rocks), row ->
                        val newCount = count.mapIndexed { c, n ->
                            when (row[c]) {
                                '.' -> n + 1
                                '#' -> 0
                                'O' -> rocks.add(Pair(r - n, c)).let { n }
                                else -> throw RuntimeException("Unknown character: ${row[c]}")
                            }
                        }.toIntArray()
                        Pair(newCount, rocks)
                    }
                }

                Direction.South -> {
                    input.foldRightIndexed(
                        Pair(
                            dotsCount,
                            mutableSetOf()
                        )
                    ) { r, row, (count, rocks) ->
                        val newCount = count.mapIndexed { c, n ->
                            when (row[c]) {
                                '.' -> n + 1
                                '#' -> 0
                                'O' -> rocks.add(Pair(r + n, c)).let { n }
                                else -> throw RuntimeException("Unknown character: ${row[c]}")
                            }
                        }.toIntArray()
                        Pair(newCount, rocks)
                    }
                }

                else -> throw RuntimeException("Direction $direction is not vertical.")
            }
        return rocks
    }

    fun tiltHorizontal(direction: Direction, input: List<String>): Set<Pair<Int, Int>> {
        val rocks = when (direction) {
            Direction.West -> {
                input.mapIndexed { r, row ->
                    row.foldIndexed(Pair(0, mutableSetOf<Pair<Int, Int>>())) { c, (n, rocks), char ->
                        val newCount = when (char) {
                            '.' -> n + 1
                            '#' -> 0
                            'O' -> rocks.add(Pair(r, c - n)).let { n }
                            else -> throw RuntimeException("Unknown character: $char")
                        }
                        Pair(newCount, rocks)
                    }.second
                }
            }

            Direction.East -> {
                input.mapIndexed { r, row ->
                    row.foldRightIndexed(Pair(0, mutableSetOf<Pair<Int, Int>>())) { c, char, (n, rocks) ->
                        val newCount = when (char) {
                            '.' -> n + 1
                            '#' -> 0
                            'O' -> rocks.add(Pair(r, c + n)).let { n }
                            else -> throw RuntimeException("Unknown character: $char")
                        }
                        Pair(newCount, rocks)
                    }.second
                }
            }

            else -> throw RuntimeException("Direction $direction is not horizontal.")
        }.flatten().toSet()
        return rocks
    }

    fun tilt(direction: Direction, input: List<String>): List<String> {
        val rocks = when {
            direction.isVertical() -> tiltVertical(direction, input)
            direction.isHorizontal() -> tiltHorizontal(direction, input)
            else -> throw IllegalArgumentException("Direction makes no sense: $direction")
        }
        val newMap = input.map { it.replace('O', '.').toCharArray() }
        rocks.forEach { (r, c) -> newMap[r][c] = 'O' }
        return newMap.map { r -> r.joinToString("") }
    }

    fun cycle(input: List<String>) =
        arrayOf(Direction.North, Direction.West, Direction.South, Direction.East)
            .fold(input) { acc, direction -> tilt(direction, acc) }

    fun countLoadAfterCycles(input: List<String>, n: Int): Int {
        val states = hashMapOf(input to Pair(0, countLoad(input)))
        var cycles = 1
        var state = input
        while (cycles++ <= n) {
            state = cycle(state)
            states[state] = Pair(cycles, countLoad(state))
        }
        return countLoad(state)
    }

    fun part1(input: List<String>): Int = countLoad(tilt(Direction.North, input))

    fun part2(input: List<String>): Int = countLoadAfterCycles(input, 1_000)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}
