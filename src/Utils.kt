import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.abs

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun Boolean.toInt() = if (this) 1 else 0

operator fun Pair<Int, Int>.plus(o: Pair<Int, Int>): Pair<Int, Int> {
    return first + o.first to second + o.second
}
operator fun Pair<Int, Int>.minus(o: Pair<Int, Int>): Pair<Int, Int> {
    return first - o.first to second - o.second
}
fun IntRange.size(): Int {
    return (last - first + 1).coerceAtLeast(0)
}

fun manhattanDistance(x: Pair<Long, Long>, y: Pair<Long, Long>): Long {
    return abs(x.first - y.first) + abs(x.second - y.second)
}
fun manhattanDistance(x: Pair<Int, Int>, y: Pair<Int, Int>): Int {
    return abs(x.first - y.first) + abs(x.second - y.second)
}

fun String.addCharAtIndex(char: Char, index: Int) =
    StringBuilder(this).apply { insert(index, char) }.toString()

inline fun <T> Iterable<T>.split(predicate: (T) -> Boolean): List<List<T>> {
    if (none()) return emptyList()
    val lists = mutableListOf<MutableList<T>>(mutableListOf())
    for (item in this) {
        if (predicate(item)) {
            lists += mutableListOf<T>()
        } else {
            lists.last() += item
        }
    }
    return lists
}

fun <T> List<T>.lowerBound(fromIndex: Int = 0, toIndex: Int = size, comparison: (T) -> Int): Int {
    return binarySearch(fromIndex, toIndex, comparison).let {
        if (it < 0) {
            return@let it
        }
        var x = it
        while (x - 1 >= 0 && comparison(this[x - 1]) == 0) {
            --x
        }
        x
    }
}

fun <T> List<T>.upperBound(fromIndex: Int = 0, toIndex: Int = size, comparison: (T) -> Int): Int {
    return binarySearch(fromIndex, toIndex, comparison).let {
        if (it < 0) {
            return@let it
        }
        var x = it
        while (x + 1 < toIndex && comparison(this[x + 1]) == 0) {
            ++x
        }
        x
    }
}

fun <T> Iterable<Iterable<T>>.transpose(): List<List<T>> {
    val lists = mutableListOf<MutableList<T>>()
    forEachIndexed { i, it ->
        it.forEachIndexed { j, v ->
            if (lists.size <= j) {
                lists.add(mutableListOf())
            }
            lists[j].add(v)
        }
    }
    check(lists.isEmpty() || lists.all { it.size == lists.first().size })
    return lists
}

fun gcd(x: Long, y: Long): Long {
    var x = x
    var y = y
    while (y != 0L) {
        val prevB = y
        y = x % y
        x = prevB
    }
    return x
}

fun lcm(x: Long, y: Long): Long {
    return x / gcd(x, y) * y
}

typealias Point = Pair<Int, Int>

enum class Direction(val repr: Pair<Int, Int>) {
    UP(-1 to 0),
    DOWN(1 to 0),
    LEFT(0 to -1),
    RIGHT(0 to 1),
    ;

    fun inverse(): Direction {
        return when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }

    fun isHorizontal(): Boolean {
        return this in listOf(Direction.LEFT, Direction.RIGHT)
    }

    fun isVertical(): Boolean {
        return !isHorizontal()
    }
}