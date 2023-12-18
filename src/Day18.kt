import kotlin.math.abs
import kotlin.math.absoluteValue

fun main() {
    fun countPoints(instructions: List<Pair<Direction, Long>>): Long {
        val points = instructions.fold(mutableListOf(0L to 0L)) { acc, (dir, steps) ->
            acc.add(acc.last() + dir.repr.cast<Int, Long>() * steps)
            acc
        }.dropLast(1)

        val area = (points + points.first()).windowed(2) { (l, r) ->
            l.first * r.second - l.second * r.first
        }.sum().absoluteValue / 2

        val b = (points + points.first()).windowed(2).sumOf { (l, r) -> abs(l.first - r.first + l.second - r.second) }

        return b + area - b / 2 + 1
    }

    fun part1(input: List<String>): Long {
        val instructions = input.map {
            val (dir, cnt) = it.split(" ").take(2)
            Direction.parseFromShortName(dir) to cnt.toLong()
        }
        return countPoints(instructions)
    }

    fun part2(input: List<String>): Long {
        val instructions = input.map {
            val hex = it.split(" ").last().dropLast(1).drop(2)
            val dir = when (hex.last()) {
                '0' -> 'R'
                '1' -> 'D'
                '2' -> 'L'
                '3' -> 'U'
                else -> error("Impossible ${hex.last()}")
            }.toString()
            Direction.parseFromShortName(dir) to hex.slice(0..<hex.length - 1).toLong(16)
        }
        return countPoints(instructions)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62L)
    check(part2(testInput) == 952408144115L)

    val input = readInput("Day18")
    part1(input).println()
    part2(input).println()
}
