
private fun findSteps(
    startNode: String,
    graph: Map<String, Pair<String, String>>,
    instructions: String,
    endNodeCriterion: (String) -> Boolean
): Int {
    var iter = 0
    var curNode = startNode
    while (!endNodeCriterion(curNode)) {
        val (l, r) = graph[curNode]!!
        val ptr = instructions[iter % instructions.length]
        curNode = if (ptr == 'L') l else r
        iter += 1
    }
    return iter
}

fun main() {
    val lineRegex = Regex("([0-9A-Z]{3}) = \\(([0-9A-Z]{3}), ([0-9A-Z]{3})\\)")

    fun part1(input: List<String>): Int {
        val instructions = input.first()
        val graph = input.drop(2).associate {
            val (k, l, r) = lineRegex.find(it)!!.groupValues.takeLast(3)
            k to (l to r)
        }

        return findSteps("AAA", graph, instructions) { it == "ZZZ" }
    }

    fun part2(input: List<String>): Long {
        val instructions = input.first()
        val graph = input.drop(2).associate {
            val (k, l, r) = lineRegex.find(it)!!.groupValues.takeLast(3)
            k to (l to r)
        }

        val steps = graph.keys
            .filter { it.endsWith("A") }
            .map { findSteps(it, graph, instructions) { n -> n.endsWith("Z") }.toLong() }

        generateSequence {  }
        return steps.reduce { acc, i -> lcm(acc, i) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 6)
    val testInput2 = readInput("Day08_test_2")
    check(part2(testInput2) == 6L)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
