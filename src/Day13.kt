private fun parseMap(map: List<String>): Set<Pair<Int, Int>> {
    return map.asSequence().flatMapIndexed { i, s ->
        s.mapIndexedNotNull { j, c ->
            if (c == '#') i to j else null
        }
    }.toSet()
}

private enum class Orientation {
    HORIZONTAL,
    VERTICAL,
    ;
}

private fun checkSymmetry(coords: Set<Pair<Int, Int>>, l: Int, orientation: Orientation, smudges: Int): Int {
    val proj = { it: Pair<Int, Int> ->
        if (orientation == Orientation.VERTICAL) it.second else it.first
    }
    val remap = { it: Pair<Int, Int>, f: (Int) -> Int ->
        if (orientation == Orientation.VERTICAL) it.first to f(it.second) else f(it.first) to it.second
    }
    val sortedCoords = coords.sortedBy { proj(it) }
    return (1..<l).firstOrNull { j ->
        val edge = sortedCoords.lowerBound { proj(it) - j }
        val (left, right) = if (j <= l - j) {
            0..<edge to edge..<sortedCoords.lowerBound { proj(it) - (2 * j) }
        } else {
            sortedCoords.lowerBound { proj(it) - (2 * j - l) }..<edge to edge..<sortedCoords.size
        }

        val leftMatches = sortedCoords.slice(left).count { coord -> coords.contains(remap(coord) { 2 * j - it - 1 }) }
        val rightMatches = sortedCoords.slice(right).count { coord -> coords.contains(remap(coord) { 2 * j - it - 1 }) }

        leftMatches to rightMatches in listOf(
            left.size() - smudges to right.size(),
            left.size() to right.size() - smudges
        )
    } ?: 0
}

fun main() {
    fun part1(input: List<String>): Int {
        val res = input.split(String::isEmpty).fold((0 to 0)) { acc, it ->
            val coords = parseMap(it)
            val vert = checkSymmetry(coords, it[0].length, Orientation.VERTICAL, 0)
            if (vert != 0) {
                acc + (vert to 0)
            } else {
                val horiz = checkSymmetry(coords, it.size, Orientation.HORIZONTAL, 0)
                acc + (0 to horiz)
            }
        }
        return res.first + res.second * 100
    }

    fun part2(input: List<String>): Int {
        val res = input.split(String::isEmpty).fold((0 to 0)) { acc, it ->
            val coords = parseMap(it)
            val vert = checkSymmetry(coords, it[0].length, Orientation.VERTICAL, 1)
            if (vert != 0) {
                acc + (vert to 0)
            } else {
                val horiz = checkSymmetry(coords, it.size, Orientation.HORIZONTAL, 1)
                acc + (0 to horiz)
            }
        }
        return res.first + res.second * 100
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
