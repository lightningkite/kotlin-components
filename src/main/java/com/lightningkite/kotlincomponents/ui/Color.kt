package com.lightningkite.kotlincomponents.ui

import android.graphics.Color

/**
 * Created by jivie on 5/23/16.
 */
fun Int.colorMultiply(amount: Float): Int {
    return Color.argb(
            Color.alpha(this),
            (Color.red(this) * amount).toInt(),
            (Color.green(this) * amount).toInt(),
            (Color.blue(this) * amount).toInt()
    )
}