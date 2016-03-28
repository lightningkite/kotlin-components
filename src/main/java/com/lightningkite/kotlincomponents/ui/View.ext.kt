package com.lightningkite.kotlincomponents.ui

import android.support.v4.view.ViewCompat
import android.view.View

/**
 * Created by jivie on 3/28/16.
 */
inline fun View.isAttachedToWindowCompat(): Boolean = ViewCompat.isAttachedToWindow(this)