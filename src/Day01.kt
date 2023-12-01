fun main() {
    fun part1(input: List<String>): Int {
        var sum = 0
        for (l in input) {
            sum += (l.find { it.isDigit() }!!.toString() + l.findLast { it.isDigit() }!!.toString()).toInt()
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        val strToDig = mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9,
        )

        val allNums = strToDig.keys + (1..9).map { it.toString() }.toSet()

        val toDigit = { digStr: String ->
            digStr.toIntOrNull() ?: strToDig[digStr]!!
        }

        var sum = 0
        for (l in input) {
            sum += toDigit(l.findAnyOf(allNums, ignoreCase = true)!!.second) * 10 + toDigit(l.findLastAnyOf(allNums, ignoreCase = true)!!.second)
        }
        return sum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)
    val testInput2 = readInput("Day01_test_2")
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
