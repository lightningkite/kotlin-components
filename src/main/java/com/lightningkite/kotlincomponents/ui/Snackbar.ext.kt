package com.lightningkite.kotlincomponents.ui

import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View
import com.lightningkite.kotlincomponents.getActivity
import org.jetbrains.anko.findOptional

/**
 * Created by josep on 3/3/2016.
 */


fun View.snackbar(text: CharSequence, duration: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {}) {
    val snack = Snackbar.make(this, text, duration)
    snack.init()
    snack.show()
}

fun View.snackbar(text: Int, duration: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {}) {
    val snack = Snackbar.make(this, text, duration)
    snack.init()
    snack.show()
}

fun Snackbar.onDismissed(lambda: (event: Int) -> Unit) {
    setCallback(object : Snackbar.Callback() {
        override fun onDismissed(snackbar: Snackbar?, event: Int) {
            lambda(event)
        }
    })
}

fun Context.snackbar(text: CharSequence, duration: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {})
        = getActivity()?.findOptional<View>(android.R.id.content)?.snackbar(text, duration, init)

fun Context.snackbar(text: Int, duration: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {})
        = getActivity()?.findOptional<View>(android.R.id.content)?.snackbar(text, duration, init)