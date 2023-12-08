fun main() {
    val regex = Regex("^Card\\s+([0-9]+): (.*) \\| (.*)$")

    fun part1(input: List<String>): Int {
        var sum = 0

        for (l in input) {
            val (winning, given) = regex.find(l)!!.let {
                val toListOfInts = { s: String -> s.trim().split(Regex("\\s+")).map { n -> n.toInt() } }
                toListOfInts(it.groupValues[2]).toSet() to toListOfInts(it.groupValues[3])
            }

            val matches = given.filter { it in winning }.size
            if (matches > 0) {
                sum += 1 shl (matches - 1)
            }
        }

        return sum
    }

    fun part2(input: List<String>): Int {
        val cardNoToCount = (1..input.size).associateWith { 1 }.toMutableMap()

        for (l in input) {
            val (cardNo, winning, given) = regex.find(l)!!.let {
                val toListOfInts = { s: String -> s.trim().split(Regex("\\s+")).map { n -> n.toInt() } }
                Triple(it.groupValues[1].toInt(), toListOfInts(it.groupValues[2]).toSet(), toListOfInts(it.groupValues[3]))
            }

            val matches = given.filter { it in winning }.size
            if (matches == 0) {
                continue
            }

            val cardNoCnt = cardNoToCount[cardNo]!!
            for (curCardNo in (cardNo + 1).coerceAtMost(input.size)..(cardNo + matches).coerceAtMost(input.size)) {
                cardNoToCount.computeIfPresent(curCardNo) { _, v -> v + cardNoCnt }
            }
        }

        val cnt = cardNoToCount.values.sum()
        println(cnt)
        println(cardNoToCount)
        return cnt
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
