import kotlin.math.absoluteValue

private enum class PipeType(val repr: Char, val dirs: List<Direction>) {
    VERTICAL('|', listOf(Direction.UP, Direction.DOWN).sorted()),
    HORIZONTAL('-', listOf(Direction.LEFT, Direction.RIGHT).sorted()),
    LEFT_DOWN('L', listOf(Direction.UP, Direction.RIGHT).sorted()),
    RIGHT_DOWN('J', listOf(Direction.LEFT, Direction.UP).sorted()),
    RIGHT_UPPER('7', listOf(Direction.LEFT, Direction.DOWN).sorted()),
    LEFT_UPPER('F', listOf(Direction.RIGHT, Direction.DOWN).sorted()),
    ;

    fun canConnectTo(dir: Direction, other: PipeType): Boolean {
        return other in mapOf(
            Direction.UP to listOf(RIGHT_UPPER, VERTICAL, LEFT_UPPER),
            Direction.DOWN to listOf(RIGHT_DOWN, VERTICAL, LEFT_DOWN),
            Direction.LEFT to listOf(LEFT_DOWN, HORIZONTAL, LEFT_UPPER),
            Direction.RIGHT to listOf(RIGHT_DOWN, HORIZONTAL, RIGHT_UPPER),
        )[dir]!!
    }

    companion object {
        private val REPR_TO_PIPE_TYPE = entries.associateBy { it.repr }
        private val DIRS_TO_PIPE_TYPE = entries.associateBy { it.dirs }

        fun parseOrNull(repr: Char): PipeType? {
            return REPR_TO_PIPE_TYPE[repr]
        }

        fun parse(dirs: List<Direction>): PipeType {
            return DIRS_TO_PIPE_TYPE[dirs.sorted()]!!
        }
    }
}

typealias Node = Pair<Int, Int>

tailrec fun findLongestCycle(
    node: Node,
    path: MutableList<Node>,
    visited: MutableSet<Node>,
    graph: Map<Node, Set<Node>>,
) {
    visited.add(node)
    path.add(node)
    val nextNode = graph[node]!!.find { it !in visited }
    if (nextNode != null) {
        findLongestCycle(nextNode, path, visited, graph)
    }
}

private fun List<String>.getSym(node: Node): Char {
    return get(node.first)[node.second]
}

private fun parseStartNodeAndGraph(input: List<String>): Pair<Node, Map<Node, Set<Node>>> {
    val len = input.first().length
    val graph = mutableMapOf<Node, MutableSet<Node>>()
    var startNode: Node = -1 to -1
    for ((i, s) in input.withIndex()) {
        for ((j, v) in s.withIndex()) {
            if (v == 'S') {
                startNode = i to j
            }
            val pipeType = PipeType.parseOrNull(v) ?: continue

            val node = i to j
            for (dir in pipeType.dirs) {
                val neighbourNode = node + dir.repr

                if (neighbourNode.first !in input.indices || neighbourNode.second !in 0..<len) {
                    continue
                }

                val neighbourValue = input.getSym(neighbourNode)
                if (neighbourValue == 'S' || PipeType.parseOrNull(neighbourValue)?.let { pipeType.canConnectTo(dir, it) } == true) {
                    graph.computeIfAbsent(node) { mutableSetOf() }.add(neighbourNode)
                    graph.computeIfAbsent(neighbourNode) { mutableSetOf() }.add(node)
                }
            }
        }
    }

    return startNode to graph
}

fun main() {
    fun part1(input: List<String>): Int {
        val (startNode, graph) = parseStartNodeAndGraph(input)
        val path = mutableListOf<Node>().also {
            findLongestCycle(startNode, it, mutableSetOf(), graph)
        }

        return (path.size + 1) / 2
    }

    fun part2(input: List<String>): Int {
        val (startNode, graph) = parseStartNodeAndGraph(input)
        val path = mutableListOf<Node>().also {
            findLongestCycle(startNode, it, mutableSetOf(), graph)
        }

        val area = (path + listOf(startNode)).windowed(2) { (l, r) ->
            l.first * r.second - l.second * r.first
        }.sum().absoluteValue / 2

        return area - path.size / 2 + 1
    }

    // test if implementation meets criteria from the description, like:
    var testInput = readInput("Day10_test")
    check(part1(testInput) == 4)
    testInput = readInput("Day10_test2")
    check(part1(testInput) == 8)

    var testInput2 = readInput("Day10_test_2")
    check(part2(testInput2) == 4)
    testInput2 = readInput("Day10_test2_2")
    check(part2(testInput2) == 8)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
