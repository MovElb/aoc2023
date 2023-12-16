private fun hash(s: String): Int {
    return s.fold(0) { acc, c ->
        ((acc + c.code) * 17) % 256
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input[0].split(",").sumOf { hash(it) }
    }

    fun part2(input: List<String>): Int {
        val boxes = buildList<MutableMap<String, Int>> {
            repeat(256) { add(mutableMapOf()) }
        }

        input[0].split(",").forEachIndexed { i, it ->
            if (it.endsWith("-")) {
                val s = it.slice(0..<(it.length - 1))
                boxes[hash(s)].remove(s)
            } else {
                val (s, length) = it.split("=")
                boxes[hash(s)][s] = length.toInt()
            }
        }

        return boxes.withIndex().sumOf { (i, m) ->
            m.entries.withIndex().sumOf { (j, v) -> (i + 1) * (j + 1) * v.value }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}
