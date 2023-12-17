import java.util.PriorityQueue

private data class NodeAStar(
    val coords: Point,
    val cost: Int,
    val parent: NodeAStar?,
    val direction: Direction,
    val stepsAtSameDir: Int,
) {
    override operator fun equals(other: Any?): Boolean {
        if (other == null || other !is NodeAStar?) {
            return false
        }
        return coords == other.coords
    }

    override fun hashCode(): Int {
        var result = coords.hashCode()
        result = 31 * result + direction.hashCode()
        result = 31 * result + stepsAtSameDir
        return result
    }
}

private fun findShortestPathAStar(
    start: NodeAStar,
    end: NodeAStar,
    graph: List<List<Int>>,
    neighbourDirectionFilter: (NodeAStar, Direction) -> Boolean,
): List<NodeAStar> {
    val pq = PriorityQueue<NodeAStar> { a, b -> a.cost - b.cost }
    pq.add(start)

    val visited = mutableSetOf<NodeAStar>()

    while (pq.isNotEmpty()) {
        val cur = pq.poll()
        if (cur == end) {
            return generateSequence(cur) { it.parent }.toList().reversed()
        }
        if (!visited.add(cur)) {
            continue
        }
        Direction.entries
            .asSequence()
            .filter { it != cur.direction.inverse() && neighbourDirectionFilter(cur, it) }
            .map { it.repr + cur.coords to it }
            .filter { (coords, _) -> coords.first in graph.indices && coords.second in graph[0].indices }
            .map { (coords, dir) ->
                NodeAStar(
                    coords = coords,
                    cost = cur.cost + graph[coords.first][coords.second],
                    parent = cur,
                    direction = dir,
                    stepsAtSameDir = if (dir == cur.direction) cur.stepsAtSameDir + 1 else 1,
                )
            }.forEach {
                pq.add(it)
            }
    }

    return emptyList()
}

fun main() {
    fun part1(input: List<String>): Int {
        val graph = input.map { it.asSequence().map { c -> c.digitToInt() }.toList() }
        val path = findShortestPathAStar(
            start = NodeAStar(
                coords = 0 to 0,
                cost = 0,
                parent = null,
                direction = Direction.RIGHT,
                stepsAtSameDir = 0,
            ),
            end = NodeAStar(
                coords = graph.indices.last to graph[0].indices.last,
                cost = Int.MAX_VALUE,
                parent = null,
                direction = Direction.UP,
                stepsAtSameDir = 3,
            ),
            graph = graph,
        ) { cur, dir -> (dir != cur.direction || cur.stepsAtSameDir < 3) }
        return path.last().cost
    }

    fun part2(input: List<String>): Int {
        val graph = input.map { it.asSequence().map { c -> c.digitToInt() }.toList() }
        val path = findShortestPathAStar(
            start = NodeAStar(
                coords = 0 to 0,
                cost = 0,
                parent = null,
                direction = Direction.RIGHT,
                stepsAtSameDir = 0,
            ),
            end = NodeAStar(
                coords = graph.indices.last to graph[0].indices.last,
                cost = Int.MAX_VALUE,
                parent = null,
                direction = Direction.UP,
                stepsAtSameDir = 3,
            ),
            graph = graph,
        ) { cur, dir ->
            when {
                cur.stepsAtSameDir < 4 -> dir == cur.direction
                cur.stepsAtSameDir < 10 -> true
                else -> dir != cur.direction
            }
        }
        return path.last().cost
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
    check(part2(testInput) == 94)

    val input = readInput("Day17")
    part1(input).println()
    part2(input).println()
}
