package com.lightningkite.kotlincomponents

import com.lightningkite.kotlincomponents.async.doAsync
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

/**
 * Created by jivie on 8/7/15.
 */

inline fun <T> retry(times: Int, delay: Long, crossinline action: () -> T?, crossinline onResult: (T?) -> Unit) {
    doAsync({
        var timesTried: Int = 0
        var result: T? = null
        while (true) {
            try {
                result = action()
            } catch(e: Exception) {
                /*squish*/
            }
            timesTried++
            if (result == null && timesTried < times) break
            try {
                Thread.sleep(delay)
            } catch(e: InterruptedException) {
                /*squish*/
            }
        }
        result
    }) {
        onResult(it)
    }
}

fun <E> List<E>.random(): E {
    return this[Math.random().times(size).toInt()]
}

fun IntRange.random(): Int {
    return Math.random().times(last - first + 1).plus(first).toInt()
}

fun ClosedRange<Float>.random(): Float {
    return Math.random().times(this.endInclusive - start).plus(start).toFloat()
}

fun ClosedRange<Double>.random(): Double {
    return Math.random().times(this.endInclusive - start).plus(start)
}

@Deprecated("Renamed to partitionToSize.", ReplaceWith("partitionToSize(maxInGroup)"))
fun <E> List<E>.splitIntoGroupsOf(maxInGroup: Int): ArrayList<List<E>> = partitionToSize(maxInGroup)
fun <E> List<E>.partitionToSize(maxInGroup: Int): ArrayList<List<E>> {
    val whole = ArrayList<List<E>>()
    var first = 0;
    var last = Math.min(first + maxInGroup, size);
    repeat(size / maxInGroup + 1) {
        whole.add(subList(first, last))
        first += maxInGroup;
        last = Math.min(first + maxInGroup, size);
    }
    return whole
}

fun Date.toCalendar(): Calendar {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal
}

var Calendar.year: Int
    get() = get(Calendar.YEAR)
    set(value) = set(Calendar.YEAR, value)

var Calendar.month: Int
    get() = get(Calendar.MONTH)
    set(value) = set(Calendar.MONTH, value)

var Calendar.dayOfMonth: Int
    get() = get(Calendar.DAY_OF_MONTH)
    set(value) = set(Calendar.DAY_OF_MONTH, value)

var Calendar.dayOfWeek: Int
    get() = get(Calendar.DAY_OF_WEEK)
    set(value) = set(Calendar.DAY_OF_WEEK, value)

var Calendar.hourOfDay: Int
    get() = get(Calendar.HOUR_OF_DAY)
    set(value) = set(Calendar.HOUR_OF_DAY, value)

var Calendar.minute: Int
    get() = get(Calendar.MINUTE)
    set(value) = set(Calendar.MINUTE, value)

var Calendar.second: Int
    get() = get(Calendar.SECOND)
    set(value) = set(Calendar.SECOND, value)

fun InputStream.toByteArray(): ByteArray {
    val output = ByteArrayOutputStream();
    try {
        val b = ByteArray(4096);
        var n = read(b);
        while (n != -1) {
            output.write(b, 0, n);
            n = read(b)
        }
        return output.toByteArray();
    } finally {
        output.close();
    }
}

fun String.toFloatMaybe():Float?{
    try{
        return toFloat()
    } catch(e:NumberFormatException){
        return null
    }
}

fun String.toDoubleMaybe():Double?{
    try{
        return toDouble()
    } catch(e:NumberFormatException){
        return null
    }
}

fun String.toIntMaybe():Int?{
    try{
        return toInt()
    } catch(e:NumberFormatException){
        return null
    }
}

fun String.toLongMaybe():Long?{
    try{
        return toLong()
    } catch(e:NumberFormatException){
        return null
    }
}

fun String.toFloatMaybe(default: Float): Float {
    try {
        return toFloat()
    } catch(e: NumberFormatException) {
        return default
    }
}

fun String.toDoubleMaybe(default: Double): Double {
    try {
        return toDouble()
    } catch(e: NumberFormatException) {
        return default
    }
}

fun String.toIntMaybe(default: Int): Int {
    try {
        return toInt()
    } catch(e: NumberFormatException) {
        return default
    }
}

fun String.toLongMaybe(default: Long): Long {
    try {
        return toLong()
    } catch(e: NumberFormatException) {
        return default
    }
}

inline fun Collection<() -> Unit>.runAll() {
    for (listener in this) {
        listener()
    }
}

inline fun <A> Collection<(A) -> Unit>.runAll(a: A) {
    for (listener in this) {
        listener(a)
    }
}

inline fun <A, B> Collection<(A, B) -> Unit>.runAll(a: A, b: B) {
    for (listener in this) {
        listener(a, b)
    }
}

inline fun <A, B, C> Collection<(A, B, C) -> Unit>.runAll(a: A, b: B, c: C) {
    for (listener in this) {
        listener(a, b, c)
    }
}

val EmailRegularExpression: Regex = "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}".toRegex(RegexOption.IGNORE_CASE)
inline fun String.isEmail(): Boolean {
    return matches(EmailRegularExpression)
}