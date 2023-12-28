import java.util.*

fun main() {
    fun part1(input: List<String>): Int {
        val graph = mutableMapOf<Point, MutableList<Point>>()
        input.asSequence().withIndex().forEach { (i, s) ->
            s.withIndex().forEach { (j, c) ->
                val curPoint = i to j
                val dirs = when (c) {
                    '.' -> Direction.entries
                    '>' -> listOf(Direction.RIGHT)
                    '<' -> listOf(Direction.LEFT)
                    '^' -> listOf(Direction.UP)
                    'V' -> listOf(Direction.LEFT)
                    else -> listOf()
                }

                for (dir in dirs) {
                    val dest = curPoint + dir.repr
                    if (!(dest.first in input.indices && dest.second in input[0].indices && input[dest.first][dest.second] != '#'))
                        continue
                    graph.computeIfAbsent(curPoint) { mutableListOf() }.add(dest)
                }
            }
        }

        val start = graph.keys.first { it.first == 0 }
        val end = graph.keys.first { it.first == input.size - 1 }

        val distances = mutableMapOf<Point, Int>()
        val q = LinkedList<Pair<Point, Int>>()
        q.add(start to 0)
        while (q.isNotEmpty()) {
            val (cur, dist) = q.pollFirst()

            if (cur == end) {
                continue
            }

            for (child in graph[cur] ?: listOf()) {
                if (child in distances && distances[child]!! <= dist + 1) {
                    continue
                }
                q.addLast(child to dist + 1)
            }
        }

//        val g = buildString {
//            for (i in input.indices) {
//                for (j in input[0].indices) {
//                    if (i to j in distances) {
//                        append('O')
//                        continue
//                    }
//
//                    if (i to j in graph) {
//                        append('.')
//                    } else {
//                        append('#')
//                    }
//                }
//                append('\n')
//            }
//        }
//
//        return distances.values.filter { (it % 2 == 0) }.size
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    check(part1(testInput) == 16)
    check(part2(testInput) == 0)

    val input = readInput("Day23")
    part1(input).println()
    part2(input).println()
}
