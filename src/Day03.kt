fun isPartNumber(row: Int, cols: IntRange, input: List<String>): Boolean {
    for (r in listOf(row - 1, row, row + 1)) {
        if (r !in input.indices) {
            continue
        }
        for (c in (cols.first() - 1)..(cols.last() + 1)) {
            if (c !in 0..<(input[0].length)) {
                continue
            }

            val sym = input[r][c]
            if (sym != '.' && !sym.isDigit()) {
                return true
            }
        }
    }

    return false
}

fun findAdjacentGears(row: Int, cols: IntRange, input: List<String>): List<Pair<Int, Int>> {
    val gears = mutableListOf<Pair<Int, Int>>()
    for (r in listOf(row - 1, row, row + 1)) {
        if (r !in input.indices) {
            continue
        }
        for (c in (cols.first() - 1)..(cols.last() + 1)) {
            if (c !in 0..<(input[0].length)) {
                continue
            }

            val sym = input[r][c]
            if (sym == '*') {
                gears.add(r to c)
            }
        }
    }
    return gears
}

fun main() {
    val regex = Regex("[0-9]+")

    fun part1(input: List<String>): Int {
        return input.mapIndexed { i, l ->
            regex.findAll(l).toList()
                .mapNotNull {
                    val num = it.groups[0]!!.value.toInt()
                    val range = it.groups[0]!!.range
                    if (isPartNumber(i, range, input)) num else null
                }
                .sum()
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val gearToNums = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()

        input.forEachIndexed { i, l ->
            regex.findAll(l).toList()
                .forEach {
                    val num = it.groups[0]!!.value.toInt()
                    val range = it.groups[0]!!.range
                    for (g in findAdjacentGears(i, range, input)) {
                        gearToNums.computeIfAbsent(g, { mutableListOf<Int>() }).add(num)
                    }
                }
        }

        return gearToNums.mapNotNull { (k, v) ->
            if (v.size != 2) {
                return@mapNotNull null
            }
            v.fold(1, Int::times)
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
