import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.abs
import kotlin.reflect.KClass

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

@Suppress("UNCHECKED_CAST")
inline fun <reified From, To> Pair<From, From>.cast(to: KClass<To>): Pair<To, To> where From : Number, To : Number {
    if (From::class == to) {
        return (this.first as To) to (this.second as To)
    }

    return when (to) {
        Byte::class -> return (first.toByte() as To) to (second.toByte() as To)
        Short::class -> return (first.toShort() as To) to (second.toShort() as To)
        Int::class -> return (first.toInt() as To) to (second.toInt() as To)
        Long::class -> return (first.toLong() as To) to (second.toLong() as To)
        Float::class -> return (first.toFloat() as To) to (second.toFloat() as To)
        Double::class -> return (first.toDouble() as To) to (second.toDouble() as To)
        else -> error("Unsupported type ${to.qualifiedName}")
    }
}

inline fun <reified From, reified To> Pair<From, From>.cast(): Pair<To, To> where From : Number, To : Number {
    return cast(To::class)
}
inline operator fun <reified T> T.plus(o: T): T where T : Number {
    return when (this) {
        is Byte -> this + o as Byte
        is Short -> this + o as Short
        is Int -> this + o as Int
        is Long -> this + o as Long
        is Float -> this + o as Float
        is Double -> this + o as Double
        else -> error("Unsupported type")
    } as T
}
inline operator fun <reified T> T.times(o: T): T where T : Number {
    return when (this) {
        is Byte -> this * o as Byte
        is Short -> this * o as Short
        is Int -> this * o as Int
        is Long -> this * o as Long
        is Float -> this * o as Float
        is Double -> this * o as Double
        else -> error("Unsupported type")
    } as T
}
inline operator fun <reified T> T.unaryMinus(): T where T : Number {
    return when (this) {
        is Byte -> -this
        is Short -> -this
        is Int -> -this
        is Long -> -this
        is Float -> -this
        is Double -> -this
        else -> error("Unsupported type")
    } as T
}

inline operator fun <reified T> Pair<T, T>.plus(o: Pair<T, T>): Pair<T, T> where T : Number {
    return first + o.first to second + o.second
}
inline operator fun <reified T> Pair<T, T>.minus(o: Pair<T, T>): Pair<T, T> where T : Number {
    return first + (-o.first) to second + (-o.second)
}
inline operator fun <reified T> Pair<T, T>.times(o: T): Pair<T, T> where T : Number {
    return first * o to second * o
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

    companion object {
        fun parseFromShortName(name: String): Direction {
            return when(name.lowercase()) {
                "u" -> UP
                "d" -> DOWN
                "l" -> LEFT
                "r" -> RIGHT
                else -> error("Invalid name $name for Direction")
            }
        }
    }
}