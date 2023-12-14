object Day12 : Puzzle {
/*
    private fun String.parse(): Pair<List<String>, List<Int>> {
        val (springs, count) = split(" ")
        return "[#?]+".toRegex().findAll(springs).map { it.value }.toList() to count.split(",").map { it.toInt() }
    }

 */
private fun String.parse(): Pair<String, List<Int>> {
    val (springs, count) = split(" ")
    return springs to count.split(",").map { it.toInt() }
}
    /*
    fun List<String>.arrangements() = sequence<List<Int>> {
        "?##?##?"
        "###?##?"
        "##?##?"
        forEach { c ->

        }
    }

    fun String.arrangements() : Sequence<String> = sequence {
        if (first() == '?') {
            drop(1).takeIf { it.isNotEmpty() }?.arrangements()?.let { yieldAll(it) }
        }
        yieldAll(drop(1).takeIf { it.isNotEmpty() }?.arrangements()?.map { "#$it" } ?: sequenceOf("#"))
    }*/

    private fun String.arrangements() : Sequence<String> = sequence {
        if(isEmpty())
            yield("")
        else {
            val char = first()
            val rest = drop(1).arrangements()
            if (char == '?')
                yieldAll(rest.flatMap { sequenceOf(".$it", "#$it") })
            else
                yieldAll(rest.map { "$char$it" })
        }
    }

    private fun String.isValid(brokenCount: List<Int>) =
        "#+".toRegex().findAll(this).map { it.value.length }.toList() == brokenCount

    override fun part1(input: List<String>): Any {
        return input
            .map { line -> line.parse() }
            .sumOf { (springs, brokenCount) -> springs.arrangements().count { it.isValid(brokenCount = brokenCount) } }
    }

    private fun String.unfold() = "$this?$this?$this?$this?$this"
    private fun List<Int>.unfold() = this+this+this+this+this

    override fun part2(input: List<String>): Any {
        return input
            .map { line -> line.parse().let { (springs, brokenCount) -> springs.unfold() to brokenCount.unfold() } }
            .sumOf { (springs, brokenCount) -> springs.arrangements().count { it.isValid(brokenCount = brokenCount) } }
    }

    /*
    fun String.buildVariants(length: Int) : List<List<String>> {
        val actualLength = count { it == '#' }
        return listOf(
            emptyList(),
        )
    }

    private fun Pair<List<String>, List<Int>>.findArrangements(prefix: String = "", row: String = "") : Int {
        val (springs, count) = this
        println("$prefix checking $springs on $count")
        if(springs.filter { it.any { it != '?' } }.isEmpty() && count.isEmpty()) return 1.also { println("$prefix +1 for $row") }
        if(springs.isEmpty() || count.isEmpty()) return 0.also { println("$prefix 0") }

        val firstSpring = springs.first()
        val firstCount = count.first()

        val restSprings = springs.drop(1)
        val restCount = count.drop(1)

        var arrangement = 0

        if(firstSpring.all { it == '?' }) { // spring is okay
            println("$prefix check okay")
            arrangement += (restSprings to count).findArrangements("$prefix ", row + firstSpring.map { '.' }.joinToString(separator = ""))
            println("$prefix back to $springs on $count")
        }

        /*
        if(firstSpring.length == firstCount) { // spring is completely damaged
            println("$prefix check damaged")
            arrangement += (restSprings to restCount).findArrangements("$prefix ")
            println("$prefix back to $springs on $count")
        }*/

        arrangement += firstSpring.indices.windowed(firstCount).let {
            val limit = "#".toRegex().findAll(firstSpring).elementAtOrNull(firstCount - 1)?.range?.start?.let { it + 2 - firstCount }
            println("$prefix limit: $limit")
            if(limit != null) it.take(limit)
            else it
        }.sumOf {
            if(firstSpring.getOrNull(it.last() + 1) == '#') {
                println("$prefix dropping range check $it")
                0
            }
            else {
                println("$prefix range check $it")
                (listOfNotNull(firstSpring.drop(it.last() + 2).takeUnless { it.isEmpty() }) + restSprings to restCount).findArrangements("$prefix ", row + firstSpring.take(it.last() + 1).map { '#' }.joinToString(separator = "", postfix = "."))
                    .also { println("$prefix back to $springs on $count") }
            }
        }
        return arrangement
        // ??? 1, x
        // #??
        // .#?
        // ..#

        // ???
        // #..
        // ...
    }

    private fun String.findArrangements() : Int {
        val (springs, count) = parse()
        println("$springs - $count")
        return 0
    }

    override fun part1(input: List<String>): Any {
        return input.sumOf { line -> line.parse().findArrangements().also { println("$line $it\n\n") } }
    }
     */
}
