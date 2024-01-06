import java.util.LinkedList
import java.util.Queue

object Day20 : Puzzle {
    const val HIGH = true
    const val LOW = false

    interface Module {
        val outputs: List<String>
        fun process(inputSignal: Boolean, source: String): Pair<Boolean, List<String>>?
    }

    data class Broadcast(override val outputs: List<String>) : Module {
        override fun process(inputSignal: Boolean, source: String): Pair<Boolean, List<String>> {
            return Pair(inputSignal, outputs)
        }
    }

    data class FlipFlop(override val outputs: List<String>, private var state: Boolean = LOW) : Module {
        override fun process(inputSignal: Boolean, source: String): Pair<Boolean, List<String>>? {
            return if (inputSignal == HIGH) null
            else {
                state = !state
                Pair(state, outputs)
            }
        }
    }

    data class Conjunction(
        override val outputs: List<String>,
        var sourceStates: MutableMap<String, Boolean>
    ) : Module {
        constructor(
            sources: Set<String>,
            outputs: List<String>,
        ) : this(outputs = outputs, sourceStates = sources.associateWith { LOW }.toMutableMap())

        override fun process(inputSignal: Boolean, source: String): Pair<Boolean, List<String>> {
            sourceStates[source] = inputSignal
            return (if (sourceStates.values.all { it == HIGH }) LOW else HIGH) to outputs
        }
    }

    data class MachineState(val modules: Map<String, Module>) {
        fun pushButton(callback: (signal: Boolean, targetModule: String) -> Unit) {
            val q: Queue<Triple<String, Boolean, String>> = LinkedList()
            q.add(Triple("button", LOW, "broadcaster"))
            do {
                val (source, signal, destination) = q.remove()
                callback(signal, destination)
                modules[destination]?.process(signal, source)?.let { (outputSignal, outputDestinations) ->
                    outputDestinations.forEach { outputDestination ->
                        q.add(Triple(destination, outputSignal, outputDestination))
                    }
                }
            } while (q.isNotEmpty())
        }
    }

    private fun String.moduleNameFromWiring() = if (this == "broadcaster") this else drop(1)

    private fun List<String>.parseModules(): Map<String, Module> {
        val unparsedModules = map { line ->
            val (module, outputs) = line.split(" -> ")
            module to outputs.split(", ")
        }

        fun findSources(moduleName: String): Set<String> =
            unparsedModules.mapNotNull { (sourceName, sourceOutputs) ->
                sourceName.takeIf { moduleName in sourceOutputs }?.moduleNameFromWiring()
            }.toSet()

        val modules = unparsedModules.associate { (module, outputs) ->
            val moduleName = module.moduleNameFromWiring()
            moduleName to when (module.first()) {
                'b' -> Broadcast(outputs)
                '%' -> FlipFlop(outputs = outputs)
                '&' -> Conjunction(sources = findSources(moduleName), outputs = outputs)
                else -> error("Unknown module: $module")
            }
        }
        return modules
    }

    override fun part1(input: List<String>): Any {
        val modules = input.parseModules()
        val state = MachineState(modules)
        var lowCount = 0L
        var highCount = 0L

        repeat(1000) {
            state.pushButton { signal, _ ->
                if (signal == LOW) lowCount++
                else highCount++
            }
        }

        return lowCount * highCount
    }

    override fun part2(input: List<String>): Any {
        val modules = input.parseModules()
        val state = MachineState(modules)
        val (preRxName, preRxModule) = state.modules.filterValues { "rx" in it.outputs }.entries.single()
        require(preRxModule is Conjunction)

        val loopEntry = mutableMapOf<String, Long>()
        val loopLength = mutableMapOf<String, Long>()
        var i = 0L
        while (loopEntry.size != preRxModule.sourceStates.size || loopLength.size != preRxModule.sourceStates.size) {
            i++
            state.pushButton { _, targetModule ->
                if (targetModule == preRxName) {
                    preRxModule.sourceStates.forEach { (sourceName, sourceState) ->
                        if (sourceState == HIGH) {
                            loopEntry.putIfAbsent(sourceName, i)?.let { entry ->
                                if (i != entry)
                                    loopLength.computeIfAbsent(sourceName) { i - entry }
                            }
                        }
                    }
                }
            }
        }

        return crt(loopEntry.map { (source, entry) -> entry to loopLength.getValue(source) })?.first ?: error("No solution found")
    }
}