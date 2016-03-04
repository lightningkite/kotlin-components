package com.lightningkite.kotlincomponents

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText

/**
 * Created by josep on 3/3/2016.
 */


fun EditText.onDone(action: (text: String) -> Unit) {
    imeOptions = EditorInfo.IME_ACTION_DONE
    setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
        if ((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            action(text.toString())
            return@OnKeyListener true;
        }
        false
    })
    setOnEditorActionListener({ v, actionId, event ->
        action(text.toString())
        true;
    })
}

fun EditText.onSend(action: (text: String) -> Unit) {
    imeOptions = EditorInfo.IME_ACTION_SEND
    setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
        if ((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            action(text.toString())
            return@OnKeyListener true;
        }
        false
    })
    setOnEditorActionListener({ v, actionId, event ->
        action(text.toString())
        true;
    })
}