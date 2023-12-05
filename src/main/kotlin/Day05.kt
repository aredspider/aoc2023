object Day05 : Puzzle {
    fun rangeWithStartAndLength(start: Long, length: Long) = LongRange(start, start + length - 1)

    data class AlmanacRange(val destination: Long, val range: LongRange) {
        companion object {
            fun fromString(input: String): AlmanacRange {
                val (destination, start, length) = input.split(" ").map { it.toLong() }
                return AlmanacRange(destination, rangeWithStartAndLength(start, length))
            }
        }

        operator fun contains(value: Long) = value in range
        fun map(value: Long) = value - range.first + destination

        fun splitContains(values: LongRange): Pair<LongRange?, List<LongRange>> {
            if (values.last < range.first || values.first > range.last) return null to listOf(values)
            if (values.first in range && values.last in range) return values to emptyList()
            if (values.first < range.first && values.last in range) return range.first..values.last to listOf(values.first..<range.first)
            if (values.last > range.last && values.first in range) return values.first..range.last to listOf((range.last + 1)..values.last)
            if (values.first < range.first && values.last > range.last) return range.first..range.last to listOf(values.first..<range.first, (range.last + 1)..values.last)
            throw IllegalStateException("Should not happen")
        }

        fun map(values: LongRange): LongRange =
            map(values.first)..map(values.last)
    }

    data class AlmanacMap(val name: String, val ranges: List<AlmanacRange>) {
        companion object {
            fun fromString(input: String): AlmanacMap {
                val lines = input.lines()
                val name = lines.first().removeSuffix(":")
                val ranges = lines.drop(1).map { AlmanacRange.fromString(it) }
                return AlmanacMap(name, ranges)
            }
        }

        fun map(value: Long) = ranges.firstOrNull { value in it }?.map(value) ?: value

        fun map(values: List<LongRange>): List<LongRange> {
            return ranges.fold(values to emptyList<LongRange>()) { (unmapped, mapped), range ->
                val splitContains = unmapped.map { range.splitContains(it) }
                splitContains.flatMap(Pair<LongRange?, List<LongRange>>::second) to (mapped + splitContains.mapNotNull { it.first?.let(range::map) })
            }.let { it.first + it.second }
        }
    }

    private fun String.parse() = split("\n\n").let { groups ->
        groups.first().removePrefix("seeds: ").split(" ").map { it.toLong() } to groups.drop(1).map { AlmanacMap.fromString(it) }
    }

    override fun part1(input: String): Any {
        val (seeds, almanacMaps) = input.parse()

        return almanacMaps.fold(seeds) { acc, almanacMap ->
            acc.map { almanacMap.map(it) }
        }.min()
    }

    override fun part2(input: String): Any {
        val (seeds, almanacMaps) = input.parse()
        val seedsRanges: List<LongRange> = seeds.chunked(2).map { (start, length) -> rangeWithStartAndLength(start, length) }

        return almanacMaps.fold(seedsRanges) { acc, almanacMap ->
            almanacMap.map(acc)
        }.minOf { it.first }
    }
}
