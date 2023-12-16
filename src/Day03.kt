fun main() { // --- Day 3: Gear Ratios ---

    val visuals = true

    fun getPartsAndGears(input: List<String>): Pair<List<Int>, List<Int>> {
        var matrix = arrayOf<Array<Char>>()
        val symbols = mutableMapOf<Char, MutableSet<Pair<Int, Int>>>()
        var numbers = arrayOf<Pair<Int, MutableSet<Pair<Int, Int>>>>()

        input.mapIndexed { r, line ->
            var num = ""
            var pts = mutableSetOf<Pair<Int, Int>>()
            var row = arrayOf<Char>()
            line.mapIndexed { c, char ->
                row += char
                if (char.isDigit()) {
                    /* digit */
                    num += char
                    pts.add(Pair(r, c))
                } else if (char != '.') {
                    /* symbol */
                    if (symbols.containsKey(char)) {
                        (symbols[char])!!.add(Pair(r, c))
                    } else {
                        symbols[char] = mutableSetOf(Pair(r, c))
                    }
                    if (num != "" && pts.isNotEmpty()) {
                        numbers += Pair(num.toInt(), pts)
                    }
                    num = ""
                    pts = mutableSetOf()
                } else {
                    /* point */
                    if (num != "" && pts.isNotEmpty()) {
                        numbers += Pair(num.toInt(), pts)
                    }
                    num = ""
                    pts = mutableSetOf()
                }
            }
            /* line ending */
            if (num != "" && pts.isNotEmpty()) {
                numbers += Pair(num.toInt(), pts)
            }
            matrix += row
        }

        val gears =
            symbols['*']!!.filter { pair -> numbers.filter { num -> (num.second intersect neighbors(pair).toSet()).isNotEmpty() }.size == 2 }
        val gearsCombined = gears.map { g ->
            Pair(
                g,
                (numbers.filter { num -> (num.second intersect neighbors(g).toSet()).isNotEmpty() }
                    .map { n -> n.first })
            )
        }
        val parts = numbers.filter { n ->
            symbols.any { (_, v) ->
                (n.second intersect v.flatMap { p -> neighbors(p) }.toSet()).isNotEmpty()
            }
        }.flatMap { n -> n.second }

        if (visuals) {
            val gearNumbers =
                numbers.filter { n -> gears.any { s -> (n.second intersect neighbors(s).toSet()).isNotEmpty() } }
                    .flatMap { num -> num.second }
            val gearParts = gearsCombined.map { (k, _) -> k }.toSet() + gearNumbers.toSet()
            val noParts =
                numbers.filter { n -> (n.second intersect gearNumbers.toSet()).isEmpty() && (n.second intersect parts.toSet()).isEmpty() }
                    .flatMap { n -> n.second }.toSet()
            print2d(
                matrix,
                mapOf(
                    Pair(Colors.Red, noParts),
                    Pair(Colors.Green, parts.toSet() - gearParts),
                    Pair(Colors.Yellow, gearParts)
                )
            )
        }

        return Pair(
            numbers.filter { n -> n.second.any { p -> p in parts } }.map { n -> n.first },
            gearsCombined.map { g -> g.second.fold(1) { acc, i -> acc * i } }
        )
    }

    fun part1(input: List<String>) = getPartsAndGears(input).first.sum()

    fun part2(input: List<String>) = getPartsAndGears(input).second.sum()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
