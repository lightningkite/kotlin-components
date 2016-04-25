package com.lightningkite.kotlincomponents.ui

import android.support.design.widget.Snackbar
import android.view.View

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

fun android.support.design.widget.Snackbar.callback(init: _Snackbar_Callback.() -> Unit) {
    val callback = _Snackbar_Callback()
    callback.init()
    setCallback(callback)
}

class _Snackbar_Callback : android.support.design.widget.Snackbar.Callback() {

    private var _onShown: ((Snackbar?) -> Unit)? = null
    private var _onDismissed: ((Snackbar?, Int) -> Unit)? = null

    override fun onShown(snackbar: Snackbar?) {
        _onShown?.invoke(snackbar)
    }

    fun onSnackbarShown(listener: (Snackbar?) -> Unit) {
        _onShown = listener
    }

    override fun onDismissed(snackbar: Snackbar?, event: Int) {
        _onDismissed?.invoke(snackbar, event)
    }

    fun onSnackbarDismissed(listener: (Snackbar?, Int) -> Unit) {
        _onDismissed = listener
    }
}