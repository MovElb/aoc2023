fun main() {
    fun solver(input: List<String>, expansionRatio: Long): Long {
        val emptyRows = input.asSequence()
            .map { s -> if (s.all { it == '.' }) 1 else 0 }
            .runningReduce(Int::plus)
            .toList()
        val emptyCols = input[0].indices.asSequence()
            .map { j -> if (input.indices.all { i -> input[i][j] == '.' }) 1 else 0 }
            .runningReduce(Int::plus)
            .toList()

        val starsCoords = input.asSequence().withIndex().flatMap { (i, s) ->
            s.asSequence().withIndex()
                .filter { (_, v) -> v == '#' }
                .map { (j, _) -> (i + (expansionRatio - 1) * emptyRows[i]) to (j + (expansionRatio - 1) * emptyCols[j]) }
        }.toList()

        return starsCoords.indices.asSequence().map { i ->
            (i + 1..<starsCoords.size).sumOf { j ->
                manhattanDistance(starsCoords[i], starsCoords[j])
            }
        }.sum()
    }


    fun part1(input: List<String>): Long {
        return solver(input, 2)
    }

    fun part2(input: List<String>): Long {
        return solver(input, 1_000_000)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}
