package com.lightningkite.kotlincomponents.ui

import android.app.Activity
import android.support.annotation.StringRes
import android.text.InputType
import android.widget.EditText
import org.jetbrains.anko.alert
import org.jetbrains.anko.editText

/**
 * Created by josep on 11/6/2015.
 */
fun Activity.inputDialog(@StringRes message: Int, @StringRes title: Int, inputType: Int = InputType.TYPE_CLASS_TEXT, onResult: (String?) -> Unit) {
    alert(message, title) {
        var input: EditText
        customView {
            input = editText {
                this.inputType = inputType
            }
        }
        positiveButton {
            onResult(input.text.toString())
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
        var input: EditText
        customView {
            input = editText {
                this.inputType = inputType
            }
        }
        positiveButton {
            onResult(input.text.toString())
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