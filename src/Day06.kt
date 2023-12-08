import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {

    val processGame = { t: Long, s: Long ->
        val isIntegerSolution = { h: Long -> (-h * h + t * h - s == 0L).toInt().toLong() }

        val d = sqrt((t * t - 4 * s).toDouble())
        val low = ceil((t - d) / 2).toLong()
        val up = floor((t + d) / 2).toLong()

        (up - low + 1 - isIntegerSolution(up) - isIntegerSolution(low)).toInt()
    }

    fun part1(input: List<String>): Int {
        val games = input.asSequence()
            .map { it.replace(Regex("[a-zA-Z:]*"), "").trim().split(Regex("\\s+")).map(String::toLong) }
            .zipWithNext { a, b -> a.zip(b) }
            .flatten()
            .toList()

        return games.map {(t, s) -> processGame(t, s)}.fold(1, Int::times)
    }

    fun part2(input: List<String>): Int {
        val (t, s) = input.asSequence()
            .map { it.replace(Regex("[a-zA-Z:\\s]*"), "").toLong() }
            .toList()
        return processGame(t, s)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
