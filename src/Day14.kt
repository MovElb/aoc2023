private typealias CharMap = List<List<Char>>

private fun CharMap.tilt(): CharMap {
    val map = this.map { it.toMutableList() }.toMutableList()
    for (j in map[0].indices) {
        var insertPos = -1
        var ptr = 0
        while (ptr < map.size) {
            val char = map[ptr][j]
            when (char) {
                '#' -> {
                    insertPos = ptr
                }
                'O' -> {
                    ++insertPos
                    map[ptr][j] = '.'
                    map[insertPos][j] = 'O'
                }
            }
            ++ptr
        }
    }
    return map
}

private fun CharMap.spin(): CharMap {
    return this
        .tilt().transpose()
        .tilt().transpose().asReversed()
        .tilt().transpose().asReversed()
        .tilt().asReversed().transpose().asReversed()
}

private fun score(map: CharMap): Int {
    return map.withIndex().sumOf { (i, l) -> (map.size - i) * l.count { it == 'O' } }
}

fun main() {
    fun part1(input: List<String>): Int {
        return score(input.map { it.toList() }.tilt())
    }

    fun part2(input: List<String>): Int {
        val ITERS = 1_000_000_000
        var map = input.map { it.toList() }
        val mapToIdx = mutableMapOf<CharMap, Int>()
        val idxToMap = mutableListOf<CharMap>()

        var range = 0..0
        for (i in 0..<ITERS) {
            val found = mapToIdx.getOrPut(map) { i }
            map = map.spin()
            idxToMap.add(map)
            if (found != i) {
                range = found..<i
                break
            }
        }

        val idx = ((ITERS - range.last - 2) % range.size()) + range.first
        return score(idxToMap[idx])
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}
