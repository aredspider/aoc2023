import kotlin.reflect.full.findParameterByName

object Day19 : Puzzle {
    data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
        companion object {
            private val regex = "^\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)}$".toRegex()
            fun parse(line: String): Part {
                val (x, m, a, s) = regex.matchEntire(line)?.groupValues?.drop(1)?.map(String::toInt) ?: error("Could not parse part: $line")
                return Part(x, m, a, s)
            }
        }

        val xmas get() = x + m + a + s
    }

    data class PartRange(val x: IntRange = 1..4000, val m: IntRange = 1..4000, val a: IntRange = 1..4000, val s: IntRange = 1..4000) {
        companion object {
            val EMPTY = PartRange(IntRange.EMPTY, IntRange.EMPTY, IntRange.EMPTY, IntRange.EMPTY)
        }

        fun isEmpty() = x.isEmpty() || m.isEmpty() || a.isEmpty() || s.isEmpty()
        val xmasVariations = listOf(x, m, a, s).map { it.last - it.first + 1L }.reduce(Long::times)
    }

    interface Rule {
        fun applies(part: Part): Boolean
        fun split(partRange: PartRange): Pair<PartRange, PartRange>
        val destination: String
    }

    data class FallbackRule(override val destination: String) : Rule {
        override fun applies(part: Part) = true
        override fun split(partRange: PartRange) = partRange to PartRange.EMPTY
    }

    data class DividerRule(val attribute: String, val operator: Char, val value: Int, override val destination: String) : Rule {
        private val partGetter = Part::class.members.find { it.name == attribute } ?: error("Unknown attribute: $attribute")
        private val partRangeGetter = PartRange::class.members.find { it.name == attribute } ?: error("Unknown attribute: $attribute")
        private val comparingFunction: (Int) -> Boolean = when (operator) {
            '<' -> { attribute -> attribute < value }
            '>' -> { attribute -> attribute > value }
            else -> error("Unknown comparator: $operator")
        }

        override fun applies(part: Part): Boolean = comparingFunction(partGetter.call(part) as Int)
        override fun split(partRange: PartRange): Pair<PartRange, PartRange> {
            val attributeRange = partRangeGetter.call(partRange) as IntRange
            val (acceptedRange, rejectedRange) = when (operator) {
                '<' -> attributeRange.first..<value to (value)..attributeRange.last
                '>' -> value + 1..attributeRange.last to attributeRange.first..value
                else -> error("Unknown comparator: $operator")
            }
            val copyParameter = partRange::copy.findParameterByName(attribute)!!
            return partRange::copy.callBy(mapOf(copyParameter to acceptedRange)) to partRange::copy.callBy(mapOf(copyParameter to rejectedRange))
        }
    }

    data class Workflow(val rules: List<Rule>) {
        companion object {
            private val ruleRegex = "([xmas])([<>])(\\d+):([a-z]+|[AR]),".toRegex()
            private val regex = "^([a-z]+)\\{(.+,)([a-z]+|[AR])}$".toRegex()
            fun parse(line: String): Pair<String, Workflow> {
                val (name, rulesStr, fallback) = (regex.matchEntire(line) ?: error("Could not parse workflow: $line")).destructured

                val rules = ruleRegex.findAll(rulesStr)
                    .map(MatchResult::destructured)
                    .map { (tested, comparator, valueStr, destination) ->
                        DividerRule(attribute = tested, operator = comparator.single(), value = valueStr.toInt(), destination = destination)
                    } + FallbackRule(destination = fallback)

                return name to Workflow(rules.toList())
            }
        }

        fun apply(part: Part): String = rules.first { it.applies(part) }.destination
        fun split(partRange: PartRange): List<Pair<String, PartRange>> =
            rules.fold(emptyList<Pair<String, PartRange>>() to partRange) { (result, partRange), rule ->
                val (acceptedRange, rejectedRange) = rule.split(partRange)
                (if(acceptedRange.isEmpty())
                    result
                else
                    result + (rule.destination to acceptedRange)) to rejectedRange
            }.first
    }

    private tailrec fun Map<String, Workflow>.isAccepted(part: Part, workflowName: String = "in"): Boolean =
        when (val next = this.getValue(workflowName).apply(part)) {
            "A" -> true
            "R" -> false
            else -> isAccepted(part, next)
        }

    override fun part1(input: String) =
        input.split("\n\n")
            .map(String::lines)
            .let { (workflows, parts) -> workflows.associate(Workflow::parse) to parts.map(Part::parse) }
            .let { (workflows, parts) ->
                parts.filter { part -> workflows.isAccepted(part) }
                    .sumOf { it.xmas }
            }

    private fun Map<String, Workflow>.findAcceptedRanges(partRange: PartRange = PartRange(), workflowName: String = "in") : Sequence<PartRange> = sequence {
        getValue(workflowName).split(partRange).forEach { (destination, range) ->
            if (destination == "A")
                yield(range)
            else if (destination != "R")
                yieldAll(findAcceptedRanges(range, destination))
        }
    }

    override fun part2(input: List<String>) =
        input
            .takeWhile { it.isNotEmpty() }
            .associate(Workflow.Companion::parse)
            .findAcceptedRanges().sumOf { it.xmasVariations }
}