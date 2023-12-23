fun main() { // --- Day 19: Aplenty ---
    data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
        fun xmas() = this.x + this.m + this.a + this.s
    }

    data class Rule(val aspect: Char, val op: Char, val threshold: Int, val source: String, val target: String) {
        constructor(source: String, target: String) : this(' ', ' ', -1, source, target)

        fun toRange(maxValue: Int) = when (op) {
            '>' -> (threshold + 1)..maxValue
            '<' -> 1..<threshold
            else -> throw RuntimeException("Unknown operator: $op")
        }

        fun apply(part: Part): String? {
            val value = when (this.aspect) {
                'x' -> part.x
                'm' -> part.m
                'a' -> part.a
                's' -> part.s
                else -> throw RuntimeException("Unknown Part aspect: ${this.aspect}")
            }
            when (op) {
                '<' -> if (value < threshold) return target
                '>' -> if (value > threshold) return target
                else -> throw RuntimeException("Unknown operator: $op")
            }
            return null
        }
    }

    data class Workflow(val name: String, val rules: List<Rule>, val default: String)

    fun parseRules(rulesString: String, source: String): Pair<List<Rule>, String> {
        val rules = rulesString.split(',').map { r ->
            val res = """^(\w)([<|>])(\d+):(\w+)$""".toRegex().find(r)?.destructured
            if (res != null) {
                val (part: String, op: String, threshold: String, target: String) = res
                Rule(part[0], op[0], threshold.toInt(), source, target)
            } else {
                Rule(source, r)
            }
        }
        return Pair(rules.subList(0, rules.size - 1), rules[rules.size - 1].target)
    }

    fun parse(input: String): Pair<Map<String, Workflow>, List<Part>> {
        val split = input.split("\n\n")
        val workflows = split[0].split("\n").map { line ->
            val (name: String, rulesRaw: String) = """^(\w+)\{(.+)}$""".toRegex().find(line)?.destructured
                ?: throw IllegalArgumentException("Can't parse $line")
            val (rules: List<Rule>, default: String) = parseRules(rulesRaw, name)
            Workflow(name, rules, default)
        }
        val parts = split[1].split("\n").map { line ->
            val (x: String, m: String, a: String, s: String) = """^\{x=(\d+),m=(\d+),a=(\d+),s=(\d+)}$""".toRegex()
                .find(line)?.destructured
                ?: throw IllegalArgumentException("Can't parse $line")
            Part(x.toInt(), m.toInt(), a.toInt(), s.toInt())
        }
        return Pair(workflows.associateBy { it.name }, parts)
    }

    fun part1(input: String): Int {
        val (workflows, parts) = parse(input)
        return parts.sumOf { part ->
            var next = "in"
            while (!(next == "A" || next == "R")) {
                val workflow = workflows.getValue(next)
                next = workflow.rules.firstNotNullOfOrNull { it.apply(part) } ?: workflow.default
            }
            if (next == "A") part.xmas()
            else 0
        }
    }

    val part2Result: MutableMap<String, ArrayList<Map<Char, ArrayList<IntRange>>>> = mutableMapOf()

    fun walk(
        source: String,
        destinations: List<String>,
        workflows: Map<String, Workflow>,
        acc: Map<Char, ArrayList<IntRange>>
    ) {
        val rules = workflows.getValue(source).rules
        if (source in destinations) {
            println("$source: $acc")
            part2Result.getOrPut(source) { arrayListOf() }.add(acc)
        } else {
            for (r in rules) {
                val bla = acc.toMutableMap()
                bla.getOrPut(r.aspect) { arrayListOf() }.add(r.toRange(4000))
                walk(r.target, destinations, workflows, bla)
            }
            walk(workflows.getValue(source).default, destinations, workflows, acc)
        }
    }

    fun part2(input: String): Long {
        val (workflows, _) = parse(input)
        val allWorkflows = (workflows + listOf(
            "A" to Workflow("A", listOf(), "A"),
            "R" to Workflow("R", listOf(), "R")
        ))

        walk("in", listOf("A", "R"), allWorkflows, mapOf())
        println(part2Result)


        val adjacencyListGraph = Graph()

        val vertices = allWorkflows.map { adjacencyListGraph.createVertex(it.key) }
        allWorkflows.forEach { (k, v) ->
            // workflow
            val source = vertices.first { it.name == k }
            v.rules.forEach { r ->
                // rule
                val destination = vertices.first { it.name == r.target }
                val range = when (r.op) {
                    '<' -> 1..<r.threshold
                    '>' -> (r.threshold + 1)..4000
                    else -> throw RuntimeException("Invalid operator ${r.op}")
                }
                val ranges = when (r.aspect) {
                    'x' -> arrayListOf(range, null, null, null)
                    'm' -> arrayListOf(null, range, null, null)
                    'a' -> arrayListOf(null, null, range, null)
                    's' -> arrayListOf(null, null, null, range)
                    else -> throw RuntimeException("Unknown aspect: ${r.aspect}")
                }
                adjacencyListGraph.addDirectedEdge(source, destination, ranges)
            }
            adjacencyListGraph.addDefaultEdge(
                source,
                vertices.first { it.name == v.default },
                1..4000
            )
            // all rules made

        }

//        adjacencyListGraph.println()
        return input.length.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputText("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 167409079868000L)

    val input = readInputText("Day19")
    part1(input).println()
    part2(input).println()
}

data class Vertex(val index: Int, val name: String)


/**
 * src -> dest
 * ranges: [
 *   [1..300, 500..3400]     // x = 0
 *   [1..3000, 3500..4000]   // m = 1
 *   [900..1750, 2300..3000] // a = 2
 *   [46..400, 2100..4000]   // s = 3
 * ]
 */
data class Edge(val source: Vertex, val destination: Vertex, val ranges: ArrayList<ArrayList<IntRange>>)

class Graph {
    private val adjacencyMap = mutableMapOf<Vertex, ArrayList<Edge>>()

    fun createVertex(name: String): Vertex {
        val vertex = Vertex(adjacencyMap.count(), name)
        adjacencyMap[vertex] = arrayListOf()
        return vertex
    }

    fun addDirectedEdge(source: Vertex, destination: Vertex, ranges: ArrayList<IntRange?>) {
        val index =
            adjacencyMap[source]?.indexOfFirst { edge -> edge.source == source && edge.destination == destination }
        if (index != null && index > -1) { // update ranges
            val updateRangeIdx = ranges.indexOfFirst { it != null }
            adjacencyMap.getValue(source)[index].ranges[updateRangeIdx] += ranges[updateRangeIdx]!!
        } else { // add edge
            val rangesMapped = ArrayList(ranges.map {
                when {
                    it == null -> arrayListOf()
                    else -> arrayListOf(it)
                }
            })
            val edge = Edge(source, destination, rangesMapped)
            adjacencyMap[source]?.add(edge)
        }
    }

    fun addDefaultEdge(source: Vertex, destination: Vertex, range: IntRange) {
        val edge = Edge(
            source,
            destination,
            arrayListOf(arrayListOf(range), arrayListOf(range), arrayListOf(range), arrayListOf(range))
        )
        adjacencyMap[source]?.add(edge)
//        val otherEdges = adjacencyMap[source]?.filter { it.source == source }
//        if (otherEdges != null) { // update ranges
//            val updateRangeIdx = ranges.indexOfFirst { it != null }
//            adjacencyMap.getValue(source)[index].ranges[updateRangeIdx] = ranges[updateRangeIdx]
//        } else { // add edge
//            val edge = Edge(source, destination, arrayListOf(range, range, range, range))
//            adjacencyMap[source]?.add(edge)
//        }
    }

    //    mutableMapOf("x" to 1..4000, "m" to 1..4000, "a" to 1..4000, "s" to 1..4000)
//    fun walk(start: String): Map<String, ArrayList<Map<Char, IntRange>>> {
////        val result = adjacencyMap.keys.associate { key ->
////            key.name to arrayListOf<Map<Char, IntRange>>()
////        }.toMutableMap()
////        result["in"] = arrayListOf(mapO('x' to 1..4000, 'm' to 1..4000, 'a' to 1..4000, 's' to 1..4000))
//        val toVisit = ArrayDeque<Pair<Vertex, ArrayList<ArrayList<IntRange>>>>()
//        val visited = mutableSetOf<Pair<Vertex, ArrayList<ArrayList<IntRange>>>>()
//        toVisit.add(
//            Pair(
//                adjacencyMap.keys.first { it.name == "in" },
//                arrayListOf(arrayListOf(1..4000), arrayListOf(1..4000), arrayListOf(1..4000), arrayListOf(1..4000))
//            )
//        )
//        while (toVisit.isNotEmpty()) {
//            val next = toVisit.removeFirst()
//            if (next.first.name == "A" || next.first.name == "R") {
//                break
//            } else {
//                visited.add(next)
//                val filtersEncountered = next.second
//                val edges = adjacencyMap.getValue(next.first)
//                toVisit.addAll(
//                    edges.mapNotNull { e ->
//                        val updatedFilters = filtersEncountered.mapIndexed { i, f ->
//                            ArrayList(f + e.ranges[i])
//                        }
//                        if (visited.contains(e.destination.name)) null else Pair(
//                            e.destination,
//                            ArrayList(updatedFilters)
//                        )
//                    })
//                // administration
//                edges.forEach { e ->
//
//                }
//            }
//        }
//    }

    override fun toString(): String {
        return buildString {
            adjacencyMap.forEach { (vertex, edges) ->
                val edgeString = edges.joinToString { it.destination.name }
                append("${vertex.name} -> [$edgeString]\n")
            }
        }
    }
}
