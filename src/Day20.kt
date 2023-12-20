fun main() { // --- Day 20: Pulse Propagation ---

    fun createNetwork(input: List<String>): Pair<ArrayList<Module>, Module> {
        val network = ArrayList<Module>(input.size)
        val conjunctionModules = mutableSetOf<ConjunctionModule>()
        input.mapTo(network) { line ->
            val parts = line.split(" -> ")
            val outputs = ArrayList(parts[1].split(", "))
            when (parts[0].first()) {
                '%' -> FlipFlopModule(parts[0].substring(1), outputs)
                '&' -> ConjunctionModule(parts[0].substring(1), outputs).also { conjunctionModules.add(it) }
                'b' -> Module(parts[0], outputs)
                else -> throw IllegalArgumentException("Invalid input $line.")
            }
        }

        val missingModules = network.map { it.instantiateOutputs(network) }.flatten()

        val button = Module("button", arrayListOf("broadcaster"))
        button.instantiateOutputs(network)
        network.addFirst(button)

        network.addAll(missingModules)

        for (m in conjunctionModules) {
            m.instantiateInputs(network)
        }

        return Pair(network, button)
    }

    fun part1(input: List<String>): Int {
        val (network, button) = createNetwork(input)
        var cycle = 0
        val finger = Module("deus ex", arrayListOf())
        while (cycle < 1000) {
            var batch = arrayListOf(Triple(finger, button, Pulse.Low))
            do {
                batch = ArrayList(batch.map { it.second.msg(it.first, it.third) }.flatten())
            } while (batch.isNotEmpty())
            cycle++
            if (network.all { m -> m.inOriginalState() }) {
                break
            }
        }
        return network.map { m -> m.counts() }
            .unzip()
            .let { (lows, highs) -> lows.sum() * (1000 / cycle) * highs.sum() * (1000 / cycle) }
    }

    fun part2(input: List<String>): Long {
        val (network, button) = createNetwork(input)

        val rx = network.find { it.name == "rx" } ?: throw RuntimeException("No module 'rx' found!")
        val conjunctionBeforeRx = network.first { it.outputs.contains(rx) }
        val monitored = network
            .filter { it.outputs.contains(conjunctionBeforeRx) }
            .associate { it.name to 0L }
            .toMutableMap()

        var cycle = 0L
        val finger = Module("deus ex", arrayListOf())
        while (monitored.values.any { it == 0L }) {
            cycle++
            var batch = arrayListOf(Triple(finger, button, Pulse.Low))
            do {
                batch.filter { (from, _, pulse) -> pulse == Pulse.High && from.name in monitored && monitored[from.name] == 0L }
                    .forEach { (from, _, _) -> monitored[from.name] = cycle }
                batch = ArrayList(batch.map { (from, to, pulse) -> to.msg(from, pulse) }.flatten())
            } while (batch.isNotEmpty())
        }

        return monitored.values.toList().leastCommonMultiple()
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day20_test1")
    check(part1(testInput1) == 32_000_000)
    val testInput2 = readInput("Day20_test2")
    check(part1(testInput2) == 11_687_500)

    val input = readInput("Day20")
    part1(input).println()
    part2(input).println()
}

open class Module(val name: String, val outputNames: ArrayList<String>) {
    private var pulseCounterLow = 0
    private var pulseCounterHigh = 0
    var outputs: ArrayList<Module> = arrayListOf()

    open fun inOriginalState(): Boolean = true

    fun instantiateOutputs(network: ArrayList<Module>): ArrayList<Module> {
        val missingModules = mutableSetOf<Module>()
        outputNames.mapTo(outputs) {
            network.find { m -> m.name == it }
                ?: Module(it, arrayListOf()).also { n -> missingModules.add(n) }
        }
        return ArrayList(missingModules)
    }

    open fun msg(from: Module, pulse: Pulse): ArrayList<Triple<Module, Module, Pulse>> {
        when (pulse) {
            Pulse.Low -> pulseCounterLow += outputs.size
            Pulse.High -> pulseCounterHigh += outputs.size
        }

        return ArrayList(outputs.map { Triple(this, it, pulse) })
    }

    fun counts() = Pair(pulseCounterLow, pulseCounterHigh)
}

class FlipFlopModule(name: String, outputNames: ArrayList<String>) : Module(name, outputNames) {
    private var turnedOn: Boolean = false

    override fun inOriginalState(): Boolean = !turnedOn

    override fun msg(from: Module, pulse: Pulse): ArrayList<Triple<Module, Module, Pulse>> {
        if (pulse == Pulse.Low) {
            val outPulse = if (turnedOn) Pulse.Low else Pulse.High
            turnedOn = !turnedOn
            return super.msg(from, outPulse)
        } else {
            return arrayListOf()
        }
    }
}

class ConjunctionModule(name: String, outputNames: ArrayList<String>) : Module(name, outputNames) {
    private var inputs: ArrayList<Module> = arrayListOf()
    private val memory: ArrayList<Pulse> = arrayListOf()

    fun instantiateInputs(network: ArrayList<Module>) {
        val foundInputs = network.filter { it.outputs.contains(this) }
        foundInputs.mapTo(memory) { Pulse.Low }
        this.inputs = ArrayList(foundInputs)
    }

    override fun inOriginalState() = memory.all { it == Pulse.Low }

    override fun msg(from: Module, pulse: Pulse): ArrayList<Triple<Module, Module, Pulse>> {
        val idx = inputs.indexOf(from)
        if (idx < 0) throw RuntimeException("Unknown input: $from")

        memory[idx] = pulse

        val outPulse = if (memory.all { p -> p == Pulse.High }) Pulse.Low else Pulse.High
        return super.msg(from, outPulse)
    }
}

enum class Pulse { Low, High }
