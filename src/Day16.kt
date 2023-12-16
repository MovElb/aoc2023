import java.time.Duration
import java.time.Instant
import java.util.LinkedList

private fun slashMove(dir: Direction): Direction {
    return when (dir) {
        Direction.UP -> Direction.RIGHT
        Direction.DOWN -> Direction.LEFT
        Direction.LEFT -> Direction.DOWN
        Direction.RIGHT -> Direction.UP
    }
}

private fun countEnergizedTiles(input: List<String>, startPoint: Point, startDir: Direction): Int {
    val visited = mutableMapOf<Point, MutableList<Direction>>()

    val q = LinkedList<Pair<Point, Direction>>()
    q.add(startPoint to startDir)
    while (q.isNotEmpty()) {
        val (point, dir) = q.removeFirst()
        if (visited[point]?.let { dir in it } == true) {
            continue
        }

        val newDirs = when (input[point.first][point.second]) {
            '/' -> listOf(slashMove(dir))
            '\\' -> listOf(slashMove(dir).inverse())
            '-' -> {
                if (dir.isHorizontal()) {
                    listOf(dir)
                } else {
                    listOf(Direction.LEFT, Direction.RIGHT)
                }
            }
            '|' -> {
                if (dir.isVertical()) {
                    listOf(dir)
                } else {
                    listOf(Direction.UP, Direction.DOWN)
                }
            }
            '.' -> listOf(dir)
            else -> error("Impossible symbol")
        }

        for (newDir in newDirs) {
            val newPoint = point + newDir.repr
            if (newPoint.first !in input.indices || newPoint.second !in input[0].indices) {
                continue
            }

            q.addLast(newPoint to newDir)
        }

        visited.computeIfAbsent(point) { mutableListOf() }.add(dir)
    }

    return visited.size
}

fun main() {
    fun part1(input: List<String>): Int {
        return countEnergizedTiles(input, 0 to 0, Direction.RIGHT)
    }

    fun part2(input: List<String>): Int {
        val lastRowIdx = input.indices.last
        val lastColIdx = input[0].indices.last

        return input.indices.flatMap { i -> input[0].indices.map { j -> i to j } }.filter {
            it.first in listOf(0, lastRowIdx)
                    || it.second in listOf(0, lastColIdx)
        }.maxOf { point ->
            val dirs = buildList {
                if (point.first == 0) {
                    add(Direction.DOWN)
                } else if (point.first == lastRowIdx) {
                    add(Direction.UP)
                }

                if (point.second == 0) {
                    add(Direction.RIGHT)
                } else if (point.second == lastColIdx) {
                    add(Direction.LEFT)
                }
            }

            dirs.maxOf { countEnergizedTiles(input, point, it) }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 46)
    check(part2(testInput) == 51)

    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}
