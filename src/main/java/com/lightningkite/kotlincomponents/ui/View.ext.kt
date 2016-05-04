package com.lightningkite.kotlincomponents.ui

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.support.v4.view.ViewCompat
import android.view.View

/**
 * Created by jivie on 3/28/16.
 */
inline fun View.isAttachedToWindowCompat(): Boolean = ViewCompat.isAttachedToWindow(this)

inline fun View.setBackground(vararg list: Drawable) {
    background = LayerDrawable(list)
}

inline fun View.setBackground(vararg list: Int) {
    background = LayerDrawable(Array(list.size) { resources.getDrawable(list[it]) })
}