fun main() {
    val regex = Regex("^Game ([0-9]+): (.*)$")

    fun part1(input: List<String>): Int {
        val colorToMaxCount = mapOf(
            "red" to 12,
            "green" to 13,
            "blue" to 14,
        )

        var sumIds = 0
        for (l in input) {
            val groups = regex.find(l)!!.groupValues
            val id = groups[1].toInt()

            val possible = groups[2].split("; ").all { sample ->
                sample.split(", ").all {
                    val (count, color) = it.split(" ")
                    count.toInt() <= colorToMaxCount[color]!!
                }
            }

            if (possible) {
                sumIds += id
            }
        }

        return sumIds
    }

    fun part2(input: List<String>): Int {
        var sum = 0
        for (l in input) {
            val groups = regex.find(l)!!.groupValues

            val colorToMinCount = mutableMapOf(
                "red" to 0,
                "green" to 0,
                "blue" to 0,
            )

            groups[2].split("; ").forEach { sample ->
                sample.split(", ").forEach {
                    val (count, color) = it.split(" ")
                    colorToMinCount.compute(color) { _, v -> maxOf(v!!, count.toInt()) }
                }
            }

            sum += colorToMinCount.values.fold(1, Int::times)
        }

        return sum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
