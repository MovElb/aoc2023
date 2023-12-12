import java.time.Duration
import java.time.Instant

private fun countVariants(
    prefix: String,
    patternIdx: Int,
    pattern: String,
    positionIdx: Int,
    positions: List<Int>,
    cache: MutableMap<Pair<Int, Int>, Long>,
): Long {
    fun String.countDials(): List<Int> = split(Regex("""\.+""")).filter { it.isNotBlank() }.map { it.length }

    when {
        positionIdx >= positions.size -> {
            val resultPositions = buildString {
                append(prefix)
                append(pattern.slice(patternIdx..<pattern.length).replace("?", "."))
            }.countDials()
            return if (resultPositions == positions) 1L else 0L
        }
        patternIdx >= pattern.length -> return 0L
    }

    var sum = 0L
    for (i in patternIdx..<pattern.length) {
        val cnt = positions[positionIdx]
        if (i + cnt > pattern.length) {
            break
        }

        if (
            pattern.slice(i..<(i + cnt)).replace("?", "#") == "#".repeat(cnt)
            && (i == patternIdx || pattern[i - 1] != '#')
            && (i + cnt >= pattern.length || pattern[i + cnt] in listOf('.', '?'))
        ) {
            val newPrefix = buildString {
                append(prefix)
                append(pattern.slice(patternIdx..<i).replace("?", "."))
                append(pattern.slice(i..<(i + cnt)).replace("?", "#"))
                if (i + cnt < pattern.length) {
                    append('.')
                }
            }

            if (newPrefix.countDials() != positions.take(positionIdx + 1)) {
                break
            }

            val key = (i + cnt + 1).coerceAtMost(pattern.length) to (positionIdx + 1).coerceAtMost(positions.size)
            val withCache = cache.getOrPut(key) {
                countVariants(
                    prefix=newPrefix,
                    patternIdx=key.first,
                    pattern=pattern,
                    positionIdx=key.second,
                    positions=positions,
                    cache=cache,
                )
            }

            sum += withCache
        }
    }

    return sum
}

fun main() {
    fun part1(input: List<String>): Long {
        val now = Instant.now()
        val cnts = input.map { s ->
            val (pat, pos) = s.split(" ")
            pat to pos.split(",").map { it.toInt() }
        }.map { (pat, pos) -> countVariants("", 0, pat, 0, pos, mutableMapOf()) }
        println("Dur ${Duration.between(now, Instant.now()).toMillis()}ms")
        return cnts.sum()
    }

    fun part2(input: List<String>): Long {
        val now = Instant.now()
        val cnts = input.map { s ->
            val (pat, pos) = s.split(" ")
            listOf(pat, pat, pat, pat, pat).joinToString("?") to listOf(pos, pos, pos, pos, pos).joinToString(",").split(",").map { it.toInt() }
        }.map { (pat, pos) -> countVariants("", 0, pat, 0, pos, mutableMapOf()) }
        println("Dur ${Duration.between(now, Instant.now()).toMillis()}ms")
        return cnts.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21L)
    check(part2(testInput) == 525152L)

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
