import java.util.LinkedList

fun main() {
    fun part1(input: List<String>, depth: Int): Int {
        var start = 0 to 0
        val graph = mutableMapOf<Point, MutableList<Point>>()
        input.asSequence().withIndex().forEach { (i, s) ->
            s.withIndex().forEach { (j, c) ->
                if (c in listOf('.', 'S')) {
                    val curPoint = i to j
                    for (dir in listOf(Direction.LEFT, Direction.UP)) {
                        val dest = curPoint + dir.repr
                        if (!(dest.first in input.indices && dest.second in input[0].indices && input[dest.first][dest.second] != '#'))
                            continue
                        graph.computeIfAbsent(curPoint) { mutableListOf() }.add(dest)
                        graph.computeIfAbsent(dest) { mutableListOf() }.add(curPoint)
                    }
                }
                if (c == 'S') {
                    start = i to j
                }
            }
        }

        val distances = mutableMapOf<Point, Int>()
        val q = LinkedList<Pair<Point, Int>>()
        q.add(start to 0)
        while (q.isNotEmpty()) {
            val (cur, dist) = q.pollLast()
            distances[cur] = dist

            if (dist == depth) {
                continue
            }

            for (child in graph[cur] ?: listOf()) {
                if (child in distances && distances[child]!! <= dist + 1) {
                    continue
                }
                q.addLast(child to dist + 1)
            }
        }

        val g = buildString {
            for (i in input.indices) {
                for (j in input[0].indices) {
                    if (i to j in distances) {
                        append('O')
                        continue
                    }

                    if (i to j in graph) {
                        append('.')
                    } else {
                        append('#')
                    }
                }
                append('\n')
            }
        }

        return distances.values.filter { (it % 2 == 0) }.size
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput, 6) == 16)
    check(part2(testInput) == 0)

    val input = readInput("Day21")
    part1(input, 64).println()
    part2(input).println()
}
