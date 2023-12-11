import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

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
