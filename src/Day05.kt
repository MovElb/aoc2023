import java.util.SortedMap
import kotlin.math.min

fun main() {
    val regex = Regex(
        """
            seeds: (?<seeds>.*)
            
            seed-to-soil map:
            (?<seedtosoil>(?:[0-9]+ [0-9]+ [0-9]+\n?)+)
            
            soil-to-fertilizer map:
            (?<soiltofertilizer>(?:[0-9]+ [0-9]+ [0-9]+\n?)+)
            
            fertilizer-to-water map:
            (?<fertilizertowater>(?:[0-9]+ [0-9]+ [0-9]+\n?)+)
            
            water-to-light map:
            (?<watertolight>(?:[0-9]+ [0-9]+ [0-9]+\n?)+)
            
            light-to-temperature map:
            (?<lighttotemperature>(?:[0-9]+ [0-9]+ [0-9]+\n?)+)
            
            temperature-to-humidity map:
            (?<temperaturetohumidity>(?:[0-9]+ [0-9]+ [0-9]+\n?)+)
            
            humidity-to-location map:
            (?<humiditytolocation>(?:[0-9]+ [0-9]+ [0-9]+\n?)+)
        """.trimIndent(),
    )

    fun findDestinationInMap(value: Long, mapping: SortedMap<Long, Pair<Long, Long>>): Long {
        mapping.headMap(value).lastEntry()?.let { lastEntry ->
            val (dst, len) = lastEntry.value
            if (value in lastEntry.key..<(lastEntry.key + len)) {
                return dst + value - lastEntry.key
            }
        }

        mapping.tailMap(value).firstEntry()?.let { firstEntry ->
            val (dst, len) = firstEntry.value
            if (value in firstEntry.key..<(firstEntry.key + len)) {
                return dst + value - firstEntry.key
            }
        }

        return value
    }

    fun findDestinationsInMap(values: LongRange, mapping: SortedMap<Long, Pair<Long, Long>>): List<LongRange> {
        val results = mutableListOf<LongRange>()
        var processed = 0L

        while (processed < values.last - values.first + 1) {
            val value = values.first + processed

            val range = mapping.headMap(value).lastEntry()?.let { lastEntry ->
                val (dst, len) = lastEntry.value
                if (value in lastEntry.key..<(lastEntry.key + len)) {
                    (dst + value - lastEntry.key)..(dst + min(len - 1, values.last - lastEntry.key))
                } else {
                    null
                }
            } ?: mapping.tailMap(value).firstEntry()?.let { firstEntry ->
                val (dst, len) = firstEntry.value
                if (value in firstEntry.key..<(firstEntry.key + len)) {
                    (dst + value - firstEntry.key)..(dst + min(len - 1, values.last - firstEntry.key))
                } else {
                    value..min(firstEntry.key - 1, values.last)
                }
            } ?: value..values.last

            processed += range.last - range.first + 1
            results.add(range)
        }

        return results
    }

    fun findDestination(seed: Long, names: List<String>, nameToMapping: Map<String, SortedMap<Long, Pair<Long, Long>>>): Long {
        var dst = seed
        for (name in names) {
            dst = findDestinationInMap(dst, nameToMapping[name]!!)
        }
        return dst
    }

    fun findDestination(seedRange: LongRange, names: List<String>, nameToMapping: Map<String, SortedMap<Long, Pair<Long, Long>>>): Long {
        var dsts = listOf(seedRange)
        for (name in names) {
            val curDsts = mutableListOf<LongRange>()
            for (dst in dsts) {
                curDsts.addAll(findDestinationsInMap(dst, nameToMapping[name]!!))
            }
            dsts = curDsts
        }
        return dsts.minOf { it.first }
    }

    val groupNames = listOf("seedtosoil", "soiltofertilizer", "fertilizertowater", "watertolight", "lighttotemperature", "temperaturetohumidity", "humiditytolocation")

    fun part1(input: List<String>): Long {
        val parsed = regex.find(input.joinToString("\n"))!!

        val seeds = parsed.groups["seeds"]!!.value.split(" ").map(String::toLong)
        val nameToMapping = mutableMapOf<String, SortedMap<Long, Pair<Long, Long>>>()
        for (groupName in groupNames) {
            val map = mutableMapOf<Long, Pair<Long, Long>>()
            for (l in parsed.groups[groupName]!!.value.split("\n")) {
                val (dst, src, len) = l.split(" ").map(String::toLong)
                map[src] = dst to len
            }
            nameToMapping[groupName] = map.toSortedMap()
        }

        return seeds.minOf { findDestination(it, groupNames, nameToMapping) }
    }

    fun part2(input: List<String>): Long {
        val parsed = regex.find(input.joinToString("\n"))!!

        val seeds = parsed.groups["seeds"]!!.value.split(" ").asSequence()
            .map(String::toLong)
            .chunked(2)
            .map { it.component1()..<(it.component1() + it.component2()) }
            .toList()
        val nameToMapping = mutableMapOf<String, SortedMap<Long, Pair<Long, Long>>>()
        for (groupName in groupNames) {
            val map = mutableMapOf<Long, Pair<Long, Long>>()
            for (l in parsed.groups[groupName]!!.value.split("\n")) {
                val (dst, src, len) = l.split(" ").map(String::toLong)
                map[src] = dst to len
            }
            nameToMapping[groupName] = map.toSortedMap()
        }

        return seeds.minOf { findDestination(it, groupNames, nameToMapping) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
