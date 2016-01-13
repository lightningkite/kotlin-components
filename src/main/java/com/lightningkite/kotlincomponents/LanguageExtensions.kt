package com.lightningkite.kotlincomponents

import com.lightningkite.kotlincomponents.async.doAsync
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * Created by jivie on 8/7/15.
 */

public inline fun <T> retry(times: Int, delay: Long, crossinline action: () -> T?, crossinline onResult: (T?) -> Unit) {
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

fun <E> List<E>.splitIntoGroupsOf(maxInGroup: Int): ArrayList<List<E>> {
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

fun <E> Collection<E>.stringJoin(separator: String, toStringFunc: (E) -> String): String {
    val builder = StringBuilder()
    for (item in this) {
        builder.append(toStringFunc(item))
        builder.append(separator)
    }
    builder.setLength(builder.length() - separator.length())
    return builder.toString()
}

fun <A, B> Collection<A>.mapOptional(toOtherFunc: (A) -> B?): ArrayList<B> {
    val results = ArrayList<B>()
    for (item in this) {
        val other = toOtherFunc(item)
        if (other != null) {
            results.add(other)
        }
    }
    return results
}

public fun Date.toCalendar(): Calendar {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal
}

public var Calendar.year: Int
    get() = get(Calendar.YEAR)
    set(value) = set(Calendar.YEAR, value)

public var Calendar.month: Int
    get() = get(Calendar.MONTH)
    set(value) = set(Calendar.MONTH, value)

public var Calendar.dayOfMonth: Int
    get() = get(Calendar.DAY_OF_MONTH)
    set(value) = set(Calendar.DAY_OF_MONTH, value)

public var Calendar.dayOfWeek: Int
    get() = get(Calendar.DAY_OF_WEEK)
    set(value) = set(Calendar.DAY_OF_WEEK, value)

public var Calendar.hourOfDay: Int
    get() = get(Calendar.HOUR_OF_DAY)
    set(value) = set(Calendar.HOUR_OF_DAY, value)

public var Calendar.minute: Int
    get() = get(Calendar.MINUTE)
    set(value) = set(Calendar.MINUTE, value)

public var Calendar.second: Int
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