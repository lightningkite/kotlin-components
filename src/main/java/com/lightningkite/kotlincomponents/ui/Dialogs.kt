package com.lightningkite.kotlincomponents.ui

import android.app.Activity
import android.text.InputType
import android.widget.EditText
import org.jetbrains.anko.alert
import org.jetbrains.anko.editText

/**
 * Creates a dialog with an input text field on it.
 * Created by josep on 11/6/2015.
 */
fun Activity.inputDialog(message: Int, title: Int, inputType: Int = InputType.TYPE_CLASS_TEXT, onResult: (String?) -> Unit) {
    alert(message, title) {
        var input: EditText? = null
        customView {
            input = editText {
                this.inputType = inputType
            }
        }
        positiveButton {
            onResult(input?.text?.toString())
        }
        negativeButton {
            onResult(null)
        }
        cancellable(true)
        onCancel {
            onResult(null)
        }
    }.show()
}

fun Activity.inputDialog(message: String, title: String, inputType: Int = InputType.TYPE_CLASS_TEXT, onResult: (String?) -> Unit) {
    alert(message, title) {
        var input: EditText? = null
        customView {
            input = editText {
                this.inputType = inputType
            }
        }
        positiveButton(android.R.string.ok) {
            onResult(input?.text?.toString())
        }
        negativeButton(android.R.string.cancel) {
            onResult(null)
        }
        cancellable(true)
        onCancel {
            onResult(null)
        }
    }.show()
}