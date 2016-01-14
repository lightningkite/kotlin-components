package com.lightningkite.kotlincomponents

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import org.jetbrains.anko.defaultSharedPreferences
import java.util.*

/**
 * Created by jivie on 7/22/15.
 */

public fun Int.alpha(alpha: Int): Int {
    return (this and 0x00FFFFFF) or (alpha shl 24)
}

public fun Int.alpha(alpha: Float): Int {
    return (this and 0x00FFFFFF) or ((alpha.coerceIn(0f, 1f) * 0xFF).toInt() shl 24)
}

public fun Int.colorMultiply(value: Double):Int{
    return Color.argb(
            Color.alpha(this),
            (Color.red(this) * value).toInt().coerceIn(0, 255),
            (Color.green(this) * value).toInt().coerceIn(0, 255),
            (Color.blue(this) * value).toInt().coerceIn(0, 255)
    )
}

public fun Calendar.modifyTimeThroughPicker(context: Context, after: (calendar: Calendar) -> Unit) {
    TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        this@modifyTimeThroughPicker.set(Calendar.HOUR_OF_DAY, hourOfDay)
        this@modifyTimeThroughPicker.set(Calendar.MINUTE, minute)
        after(this@modifyTimeThroughPicker)
    }, this.get(Calendar.HOUR_OF_DAY), this.get(Calendar.MINUTE), false).show()
}

public fun Calendar.modifyDateThroughPicker(context: Context, after: (calendar: Calendar) -> Unit) {
    DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                this@modifyDateThroughPicker.set(Calendar.YEAR, year)
                this@modifyDateThroughPicker.set(Calendar.MONTH, monthOfYear)
                this@modifyDateThroughPicker.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                after(this@modifyDateThroughPicker)
            },
            this.get(Calendar.YEAR),
            this.get(Calendar.MONTH),
            this.get(Calendar.DAY_OF_MONTH)
    ).show()
}

public fun Context.onceEver(name: String, action: () -> Unit) {
    val prefs = defaultSharedPreferences
    if (!prefs.contains(name)) {
        prefs.edit().putBoolean(name, true).commit()
        action()
    }
}

public fun Context.untilEver(name: String, condition: () -> Boolean, action: () -> Unit) {
    val prefs = defaultSharedPreferences
    if (!prefs.contains(name)) {
        action()
        if (condition()) {
            prefs.edit().putBoolean(name, true).commit()
        }
    }
}