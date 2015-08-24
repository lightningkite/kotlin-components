package com.lightningkite.kotlincomponents

import java.util.Calendar
import java.util.Date

/**
 * Created by jivie on 8/7/15.
 */
public fun <T> T.run(runFunc: T.() -> Unit): T {
    runFunc()
    return this
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

public fun Date.toCalendar(): Calendar {
    val cal = Calendar.getInstance()
    cal.setTime(this)
    return cal
}