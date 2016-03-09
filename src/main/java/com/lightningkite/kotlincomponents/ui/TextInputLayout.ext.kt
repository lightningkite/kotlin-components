package com.lightningkite.kotlincomponents.ui

import android.support.design.widget.TextInputLayout

/**
 * Created by josep on 3/3/2016.
 */
var TextInputLayout.hintResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        hint = resources.getString(value)
    }
var TextInputLayout.errorResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        error = resources.getString(value)
    }