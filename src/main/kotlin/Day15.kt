object Day15 : Puzzle {
    private fun String.hash() = fold(0) { currentValue, char -> ((currentValue + char.code) * 17) % 256 }

    override fun part1(input: String) =
        input.split(",").sumOf { step -> step.hash() }

    override fun part2(input: String) =
        input.split(",").fold(List(256) { LinkedHashMap<String, Int>() }) { boxes, it ->
            if (it.endsWith("-")) {
                val label = it.dropLast(1)
                boxes[label.hash()] -= label
            } else {
                val (label, focalLength) = it.split("=")
                boxes[label.hash()] += label to focalLength.toInt()
            }
            boxes
        }.withIndex().sumOf { (boxNumber, box) ->
            box.entries.withIndex().sumOf { (slotNumber, lens) ->
                (boxNumber + 1) * (slotNumber + 1) * lens.value
            }
        }
}