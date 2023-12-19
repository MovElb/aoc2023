private sealed class RuleResult {
    data object Accepted : RuleResult()
    data object Rejected : RuleResult()
    data class NextRule(val name: String) : RuleResult()

    companion object {
        fun fromString(s: String): RuleResult {
            return when (s) {
                "A" -> Accepted
                "R" -> Rejected
                else -> NextRule(s)
            }
        }
    }
}

private data class SubRule(
    val type: String,
    val sign: String,
    val num: Int,
    val result: RuleResult,
) {
    fun match(part: Map<String, Int>): RuleResult? {
        val partNum = part[type]!!
        val matched = when (sign) {
            "<" -> partNum < num
            ">" -> partNum > num
            else -> error("Unsupported sign")
        }

        if (matched) {
            return result
        }
        return null
    }
}

private data class Workflow(val subRules: List<SubRule>, val elseResult: RuleResult) {
    fun match(part: Map<String, Int>): RuleResult {
        return subRules.asSequence().mapNotNull { it.match(part) }.firstOrNull() ?: elseResult
    }
}

private fun computeAcceptableParts(
    name: String,
    tree: Map<String, Workflow>,
    bounds: Map<String, IntRange>,
): Long {
    val workflow = tree[name]!!

    var sum = 0L
    val newBounds = bounds.toMutableMap()
    for (rule in workflow.subRules) {
        val curBound = newBounds[rule.type]!!
        val (matchingBound, notMatchingBound) = when (rule.sign) {
            ">" -> (maxOf(curBound.first, rule.num + 1)..curBound.last) to (curBound.first..minOf(curBound.last, rule.num))
            "<" -> (curBound.first..minOf(curBound.last, rule.num - 1)) to (maxOf(curBound.first, rule.num)..curBound.last)
            else -> error("Unsupported")
        }

        newBounds[rule.type] = matchingBound

        sum += when (rule.result) {
            RuleResult.Accepted -> newBounds.values.asSequence().map { it.size().toLong() }.reduce(Long::times)
            RuleResult.Rejected -> 0L
            is RuleResult.NextRule -> computeAcceptableParts(rule.result.name, tree, newBounds)
        }

        newBounds[rule.type] = notMatchingBound
    }

    sum += when (workflow.elseResult) {
        RuleResult.Accepted -> newBounds.values.asSequence().map { it.size().toLong() }.reduce(Long::times)
        RuleResult.Rejected -> 0L
        is RuleResult.NextRule -> computeAcceptableParts(workflow.elseResult.name, tree, newBounds)
    }

    return sum
}

fun main() {
    val ruleRegex = Regex("""([a-z]+)\{(.*),([a-zA-Z]+)\}""")
    val subRuleRegex = Regex("""([xmas])([<>])([0-9]+):([A-Za-z]+)""")

    fun parseWorkflows(input: List<String>): Map<String, Workflow> {
        return input.asSequence().takeWhile(String::isNotEmpty).associate { s ->
            val (_, name, subrules, elseResult) = ruleRegex.find(s)!!.groupValues
            val subRules = subrules.split(",").map {
                val (_, type, sign, num, result) = subRuleRegex.find(it)!!.groupValues
                SubRule(type, sign, num.toInt(), RuleResult.fromString(result))
            }
            name to Workflow(subRules, RuleResult.fromString(elseResult))
        }
    }

    fun part1(input: List<String>): Int {
        val nameToWorkflow = parseWorkflows(input)

        return input.asSequence().dropWhile(String::isNotEmpty).drop(1).map { s ->
            s.substringAfter("{").substringBefore("}").split(",").associate {
                val (type, value) = it.split("=")
                type to value.toInt()
            }
        }.filter {
            generateSequence(nameToWorkflow["in"]!!.match(it)) { r ->
                when (r) {
                    RuleResult.Accepted,
                    RuleResult.Rejected,
                    -> null
                    is RuleResult.NextRule -> nameToWorkflow[r.name]!!.match(it)
                }
            }.last() is RuleResult.Accepted
        }.sumOf { it.values.sum() }
    }

    fun part2(input: List<String>): Long {
        return computeAcceptableParts(
            name = "in",
            tree = parseWorkflows(input),
            bounds = mapOf(
                "x" to 1..4000,
                "m" to 1..4000,
                "a" to 1..4000,
                "s" to 1..4000,
            ),
        )
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 167409079868000L)

    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}
