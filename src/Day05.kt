import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.stream.LongStream

suspend fun main() { // --- Day 5: If You Give A Seed A Fertilizer ---
    data class Mapping(val src: Long, val dest: Long, val rng: Long) {
        val delta = dest - src
    }

    fun parseInput(input: String): Pair<List<Long>, List<List<Mapping>>> {
        val split = input.split("\n\n").map { l ->
            l.slice(l.indexOfFirst { it.isDigit() }..<l.length)
        }
        val seeds = split[0].split(" ").map { it.toLong() }
        val mappingsList = split.subList(1, split.size).map { l ->
            l.split("\n").map {
                it.split(" ").map { n -> n.toLong() }
                    .let { destSrcRng -> Mapping(destSrcRng[1], destSrcRng[0], destSrcRng[2]) }
            }.sortedBy { t -> t.src }
        }
        return Pair(seeds, mappingsList)
    }

    fun part1(input: String): Long {
        val (seeds, mappingsList) = parseInput(input)
        return mappingsList.fold(seeds) { acc, mappings ->
            acc.map { n ->
                mappings.find { m -> m.src <= n && (n - m.src) < m.rng }?.let { map -> n + map.delta } ?: n
            }
        }.min()
    }

    suspend fun part2(input: String): Long {
        val (seeds, mappingsList) = parseInput(input)
        val ranges = seeds.chunked(2).map { (start, length) -> LongStream.range(start, start + length) }
        val res = coroutineScope {
            ranges.map { r ->
                async {
                    mappingsList.fold(r) { acc, mappings ->
                        acc.map { n ->
                            mappings.find { m -> m.src <= n && (n - m.src) < m.rng }?.let { map -> n + map.delta } ?: n
                        }
                    }.min()
                }
            }
        }.awaitAll()
        return res.minOfOrNull { x -> x.asLong } ?: 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputText("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInputText("Day05")
    part1(input).println()
    part2(input).println()
}
