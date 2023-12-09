fun main() {
    fun part1(input: List<String>): Int {
        return input.map { l ->
            val nums = l.split(" ").map { it.toInt() }

            val observations = generateSequence(nums) {
                if (it.all { v -> v == 0 }) {
                    return@generateSequence null
                }

                it.asSequence().take(it.size - 1)
                    .zip(it.asSequence().drop(1))
                    .map { (l, r) -> r - l }
                    .toList()
            }.toList()

            observations.reversed().fold(0) { acc, v -> acc + v.last() }
        }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.map { l ->
            val nums = l.split(" ").map { it.toInt() }

            val observations = generateSequence(nums) {
                if (it.all { v -> v == 0 }) {
                    return@generateSequence null
                }

                it.asSequence().take(it.size - 1)
                    .zip(it.asSequence().drop(1))
                    .map { (l, r) -> r - l }
                    .toList()
            }.toList()

            observations.reversed().fold(0) { acc, v -> v.first() - acc }
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
