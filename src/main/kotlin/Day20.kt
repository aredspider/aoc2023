import java.util.LinkedList
import java.util.Queue

object Day20 : Puzzle {
    const val HIGH = true
    const val LOW = false

    interface Module<T : Module<T>> {
        val outputs: List<String>
        fun process(inputSignal: Boolean, source: String): Pair<Boolean, List<String>>?
        fun clone(): T
    }

    data class Broadcast(override val outputs: List<String>) : Module<Broadcast> {
        override fun process(inputSignal: Boolean, source: String): Pair<Boolean, List<String>> {
            return Pair(inputSignal, outputs)
        }

        override fun clone(): Broadcast = copy()
    }

    data class FlipFlop(val sources: Set<String>, override val outputs: List<String>, private var state: Boolean = LOW) : Module<FlipFlop> {
        override fun process(inputSignal: Boolean, source: String): Pair<Boolean, List<String>>? {
            return if (inputSignal == HIGH) null
            else {
                state = !state
                Pair(state, outputs)
            }
        }

        override fun clone(): FlipFlop = copy()
    }

    data class Conjunction(val sources: Set<String>, override val outputs: List<String>, private var sourceStates: MutableMap<String, Boolean> = sources.associateWith { LOW }.toMutableMap()) : Module<Conjunction> {
        override fun process(inputSignal: Boolean, source: String): Pair<Boolean, List<String>> {
            sourceStates[source] = inputSignal
            return (if(sourceStates.values.all { it == HIGH }) LOW else HIGH) to outputs
        }

        override fun clone(): Conjunction = copy(sourceStates = sourceStates.toMutableMap())
    }

    data class MachineState(val modules: Map<String, Module<*>>) {
        fun pushButton() : Pair<Long, Long> {
            var lowSignals = 0L
            var highSignals = 0L
            val q: Queue<Triple<String, Boolean, String>> = LinkedList()
            q.add(Triple("button", LOW, "broadcaster"))
            do {
                val (source, signal, destination) = q.remove()
                if(signal == LOW && destination == "rx") reached = true
                debugPrint(source, signal, destination)
                if(signal == LOW) lowSignals++
                else highSignals++
                modules[destination]?.process(signal, source)?.let { (outputSignal, outputDestinations) ->
                    outputDestinations.forEach { outputDestination ->
                        q.add(Triple(destination, outputSignal, outputDestination))
                    }
                }
            } while(q.isNotEmpty())
            return lowSignals to highSignals
        }

        fun clone(): MachineState = MachineState(modules = modules.mapValues { (_, module) -> module.clone() })
    }

    private var debugPrintCounter = 0
    fun debugPrint(source: String, signal: Boolean, destination: String) {
        if(debugPrintCounter > 100) return
        debugPrintCounter++
        println("$source -${if(signal == LOW) "low" else "high"}-> $destination")
    }

    private fun String.moduleNameFromWiring() = if(this == "broadcaster") this else drop(1)

    private fun List<String>.parseModules(): Map<String, Module<*>> {
        val unparsedModules = map { line ->
            val (module, outputs) = line.split(" -> ")
            module to outputs.split(", ")
        }

        fun findSources(moduleName: String): Set<String> =
            unparsedModules.mapNotNull { (sourceName, sourceOutputs) -> sourceName.takeIf { moduleName in sourceOutputs }?.moduleNameFromWiring() }.toSet()

        val modules = unparsedModules.associate { (module, outputs) ->
            val moduleName = module.moduleNameFromWiring()
            moduleName to when (module.first()) {
                'b' -> Broadcast(outputs)
                '%' -> FlipFlop(sources = findSources(moduleName), outputs = outputs)
                '&' -> Conjunction(sources = findSources(moduleName), outputs = outputs)
                else -> error("Unknown module: $module")
            }
        }
        return modules
    }

    override fun part1(input: List<String>): Any {
        debugPrintCounter = 0

        val modules = input.parseModules()

        return (1..1000).fold(Triple(MachineState(modules), 0L, 0L)) { (prevState, prevLowCount, prevHighCount), _ ->
            val state = prevState.clone()
            val (lowCount, highCount) = state.pushButton()

            if(debugPrintCounter < 100) println()

            Triple(state, prevLowCount + lowCount, prevHighCount + highCount)
        }.let { (_, lowCount, highCount) -> lowCount * highCount }
    }

    var reached = false

    override fun part2(input: List<String>): Any {
        reached = false
        val modules = input.parseModules()

        return (1..Long.MAX_VALUE).fold(Triple(MachineState(modules), 0L, 0L)) { (prevState, prevLowCount, prevHighCount), i ->
            val state = prevState.clone()
            val (lowCount, highCount) = state.pushButton()

            if(debugPrintCounter < 100) println()

            if(reached) return i

            Triple(state, prevLowCount + lowCount, prevHighCount + highCount)
        }.let { (_, lowCount, highCount) -> lowCount * highCount }
    }
}