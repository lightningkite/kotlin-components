package com.lightningkite.kotlincomponents

import android.content.Context
import org.jetbrains.anko.vibrator

/**
 * Created by jivie on 8/17/15.
 */
public fun Context.vibrate(pattern: String) {
    val longs = LongArray(pattern.length() * 2)
    var index = 0
    for (char in pattern) {
        when (char) {
            '.' -> {
                longs[index++] = 200
                longs[index++] = 100
            }
            '-' -> {
                longs[index++] = 200
                longs[index++] = 300
            }
            else -> {
                longs[index++] = 200
                longs[index++] = 0
            }
        }
    }
    vibrator.vibrate(longs, -1)
}