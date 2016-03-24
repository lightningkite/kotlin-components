package com.lightningkite.kotlincomponents

import android.graphics.Typeface
import android.widget.TextView
import java.util.*

/**
 * Created by josep on 3/3/2016.
 */


var TextView.textColorResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        setTextColor(resources.getColor(value))
    }
var TextView.hintTextColorResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        setHintTextColor(resources.getColor(value))
    }
var TextView.textColorsResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        setTextColor(resources.getColorStateList(value))
    }
var TextView.hintTextColorsResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        setHintTextColor(resources.getColorStateList(value))
    }

val fontCache: HashMap<String, Typeface> = HashMap()
fun TextView.setFont(fileName: String) {
    typeface = fontCache[fileName] ?: {
        val font = Typeface.createFromAsset(context.assets, fileName)
        fontCache[fileName] = font
        font
    }()
}