import java.util.EnumMap
import java.util.LinkedList

private enum class Signal {
    LOW,
    HIGH,
    ;
}

private sealed interface Module {
    fun forward(from: String, value: Signal): Signal?

    data object Broadcaster: Module {
        override fun forward(from: String, value: Signal): Signal {
            return value
        }
    }

    data class FlipFlop(
        private var enabled: Boolean = false,
    ) : Module {
        override fun forward(from: String, value: Signal): Signal? {
            return when (value) {
                Signal.HIGH -> null
                Signal.LOW -> {
                    val res = if (enabled) Signal.LOW else Signal.HIGH
                    enabled = !enabled
                    res
                }
            }
        }
    }

    data class Conjuction(
        private val memory: MutableMap<String, Signal> = mutableMapOf()
    ) : Module {
        override fun forward(from: String, value: Signal): Signal {
            memory[from] = value
            return if (memory.all { it.value == Signal.HIGH }) {
                Signal.LOW
            } else {
                Signal.HIGH
            }
        }

        fun addInput(from: String) {
            memory[from] = Signal.LOW
        }
    }

    data class OneOffSwitch(
        private var enabled: Boolean = false
    ) : Module {
        override fun forward(from: String, value: Signal): Signal? {
            if (value == Signal.LOW) {
                enabled = !enabled
            }
            return if (!enabled) null else value
        }

        fun isEnabled(): Boolean = enabled
    }

    data object Sink : Module {
        override fun forward(from: String, value: Signal): Signal? {
            return null
        }
    }
}

private fun parseGraph(input: List<String>): Map<String, Pair<Module, List<String>>> {
    val nameToInputNames = mutableMapOf<String, MutableList<String>>()
    val nameToModuleWithOutputs = mutableMapOf<String, Pair<Module, List<String>>>()

    for (l in input) {
        val (nameWithPrefix, outputString) = l.split(" -> ")
        val module = when {
            nameWithPrefix == "broadcaster" -> Module.Broadcaster
            nameWithPrefix.startsWith('%') -> Module.FlipFlop()
            nameWithPrefix.startsWith('&') -> Module.Conjuction()
            else -> Module.Sink
        }

        val name = nameWithPrefix.replace("%", "").replace("&", "")
        val outputs = outputString.split(", ")

        check(name !in nameToModuleWithOutputs) { name }
        nameToModuleWithOutputs[name] = module to outputs

        for (o in outputs) {
            nameToInputNames.computeIfAbsent(o) { mutableListOf() }.add(name)
        }
    }

    for ((name, moduleWithIOutputs) in nameToModuleWithOutputs) {
        val module = moduleWithIOutputs.first
        if (module !is Module.Conjuction) {
            continue
        }

        for (inp in nameToInputNames[name]!!) {
            module.addInput(inp)
        }
    }
    return nameToModuleWithOutputs
}

private fun countSignals(
    graph: Map<String, Pair<Module, List<String>>>,
    stats: EnumMap<Signal, Long>,
) {
    val q = LinkedList<Triple<String, String, Signal>>()
    q.add(Triple("", "broadcaster", Signal.LOW))

    while (q.isNotEmpty()) {
        val (from, to, signal) = q.pollFirst()
        stats.compute(signal) { _, v -> (v ?: 0L) + 1 }
        val (module, outputs) = graph[to] ?: continue
        val newSignal = module.forward(from, signal) ?: continue
        for (o in outputs) {
            q.addLast(Triple(to, o, newSignal))
        }
    }
}

fun main() {
    fun part1(input: List<String>): Long {
        val graph = parseGraph(input)
        val stats = EnumMap<Signal, Long>(Signal::class.java)
        repeat(1000) { countSignals(graph, stats) }
        return stats.values.reduce(Long::times)
    }

    fun part2(input: List<String>): Long {
        val switch = Module.OneOffSwitch()
        val graph = parseGraph(input) + ("rx" to (switch to listOf()))
        val stats = EnumMap<Signal, Long>(Signal::class.java)

        var cnt = 0L
        while (!switch.isEnabled()) {
            ++cnt
            countSignals(graph, stats)
        }

        return cnt
    }

    // test if implementation meets criteria from the description, like:
    var testInput = readInput("Day20_test")
    check(part1(testInput) == 32000000L)
    testInput = readInput("Day20_test2")
    check(part1(testInput) == 11687500L)

    val input = readInput("Day20")
    part1(input).println()
    part2(input).println()
}
