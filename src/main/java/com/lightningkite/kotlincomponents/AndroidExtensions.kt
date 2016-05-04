package com.lightningkite.kotlincomponents

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import org.jetbrains.anko.defaultSharedPreferences
import java.util.*

/**
 * Created by jivie on 7/22/15.
 */

inline fun Int.alpha(alpha: Int): Int {
    return (this and 0x00FFFFFF) or (alpha shl 24)
}

inline fun Int.alpha(alpha: Float): Int {
    return (this and 0x00FFFFFF) or ((alpha.coerceIn(0f, 1f) * 0xFF).toInt() shl 24)
}

inline fun Int.colorMultiply(value: Double): Int {
    return Color.argb(
            Color.alpha(this),
            (Color.red(this) * value).toInt().coerceIn(0, 255),
            (Color.green(this) * value).toInt().coerceIn(0, 255),
            (Color.blue(this) * value).toInt().coerceIn(0, 255)
    )
}

inline fun Context.timePicker(start: Calendar, crossinline after: (Calendar) -> Unit) {
    TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        start.set(Calendar.HOUR_OF_DAY, hourOfDay)
        start.set(Calendar.MINUTE, minute)
        after(start)
    }, start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE), false).show()
}

inline fun Context.datePicker(start: Calendar, crossinline after: (Calendar) -> Unit) {
    DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                start.set(Calendar.YEAR, year)
                start.set(Calendar.MONTH, monthOfYear)
                start.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                after(start)
            },
            start.get(Calendar.YEAR),
            start.get(Calendar.MONTH),
            start.get(Calendar.DAY_OF_MONTH)
    ).show()
}

inline fun Context.onceEver(name: String, action: () -> Unit) {
    val prefs = defaultSharedPreferences
    if (!prefs.contains(name)) {
        prefs.edit().putBoolean(name, true).commit()
        action()
    }
}

inline fun Context.untilEver(name: String, condition: () -> Boolean, action: () -> Unit) {
    val prefs = defaultSharedPreferences
    if (!prefs.contains(name)) {
        action()
        if (condition()) {
            prefs.edit().putBoolean(name, true).commit()
        }
    }
}

inline fun versionOn(version: Int, action: () -> Unit) {
    if (Build.VERSION.SDK_INT >= version) {
        action()
    }
}

inline fun <T> versionOn(version: Int, action: () -> T, otherwise: () -> T): T {
    return if (Build.VERSION.SDK_INT >= version) {
        action()
    } else {
        otherwise()
    }
}