package com.lightningkite.kotlincomponents

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.TimePicker
import java.util.Calendar

/**
 * Created by jivie on 7/22/15.
 */

public fun Int.alpha(alpha: Int): Int {
    return (this and 0x00FFFFFF) or (alpha shl 24)
}

public fun Int.alpha(alpha: Float): Int {
    return (this and 0x00FFFFFF) or ((alpha.coerceIn(0f, 1f) * 0xFF).toInt() shl 24)
}

public fun Calendar.modifyTimeThroughPicker(context: Context, after: (calendar: Calendar) -> Unit) {
    TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            this@modifyTimeThroughPicker.set(Calendar.HOUR_OF_DAY, hourOfDay)
            this@modifyTimeThroughPicker.set(Calendar.MINUTE, minute)
            after(this@modifyTimeThroughPicker)
        }
    }, this.get(Calendar.HOUR_OF_DAY), this.get(Calendar.MINUTE), false).show()
}

public fun Calendar.modifyDateThroughPicker(context: Context, after: (calendar: Calendar) -> Unit) {
    DatePickerDialog(
            context,
            object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                    this@modifyDateThroughPicker.set(Calendar.YEAR, year)
                    this@modifyDateThroughPicker.set(Calendar.MONTH, monthOfYear)
                    this@modifyDateThroughPicker.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    after(this@modifyDateThroughPicker)
                }
            },
            this.get(Calendar.YEAR),
            this.get(Calendar.MONTH),
            this.get(Calendar.DAY_OF_MONTH)
    ).show()
}