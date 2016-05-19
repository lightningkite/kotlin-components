package com.lightningkite.kotlincomponents.ui

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.lightningkite.kotlincomponents.selectableItemBackgroundBorderlessResource
import com.lightningkite.kotlincomponents.textColorResource
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCStack
import com.lightningkite.kotlincomponents.viewcontroller.implementations.dialog
import org.jetbrains.anko.*

private inline fun ViewGroup.MarginLayoutParams.standardMargins(ctx: Context) {
    leftMargin = ctx.dip(16)
    rightMargin = ctx.dip(16)
    topMargin = ctx.dip(8)
    bottomMargin = ctx.dip(8)
}

private inline fun TextView.styleTitle() {
    textSize = 18f
    setTypeface(null, android.graphics.Typeface.BOLD)
    textColorResource = android.R.color.primary_text_light
}

private inline fun TextView.styleMessage() {
    textSize = 16f
    textColorResource = android.R.color.secondary_text_light
}

private inline fun Button.styleNormal() {
    textSize = 16f
    textColorResource = android.R.color.secondary_text_light
    setAllCaps(true)
    backgroundResource = selectableItemBackgroundBorderlessResource
}

private inline fun Button.styleDestructive() {
    textSize = 16f
    textColor = Color.RED
    setAllCaps(true)
}

/**
 * Creates a psuedo-dialog that is actually an activity.  Significantly more stable and safe.
 */
fun Activity.standardDialog(
        title: String?,
        message: String,
        dismissOnClickOutside: Boolean = true,
        buttons: List<Pair<String, (VCStack) -> Unit>>,
        content: ViewGroup.(VCStack) -> View
) {
    return dialog(dismissOnClickOutside, layoutParamModifier = { width = matchParent }) { ui, vcStack ->
        ui.scrollView {
            verticalLayout {
                //title
                textView(title) {
                    styleTitle()
                    if (title.isNullOrEmpty()) {
                        visibility = View.GONE
                    }
                }.lparams(matchParent, wrapContent) {
                    standardMargins(context)
                    topMargin = dip(16)
                }

                //message
                textView(message) {
                    styleMessage()
                }.lparams(matchParent, wrapContent) {
                    standardMargins(context)
                }

                //custom content
                content(vcStack).lparams(matchParent, wrapContent) {
                    standardMargins(context)
                }

                //buttons
                linearLayout {
                    gravity = Gravity.END
                    for ((buttonName, action) in buttons) {
                        button(buttonName) {
                            styleNormal()
                            onClick {
                                action(vcStack)
                            }
                        }.lparams(wrapContent, wrapContent) {
                            standardMargins(context)
                        }
                    }
                }.lparams(matchParent, wrapContent)
            }
        }
    }
}


/**
 * Creates a dialog with an input text field on it.
 */
fun Activity.inputDialog(title: Int, message: Int, hint: Int, inputType: Int = InputType.TYPE_CLASS_TEXT, canCancel: Boolean = true, validation: (String) -> Int? = { null }, onResult: (String?) -> Unit) {
    return inputDialog(resources.getString(title), resources.getString(message), resources.getString(hint), inputType, canCancel, validation, onResult)
}


/**
 * Creates a dialog with an input text field on it.
 */
fun Activity.inputDialog(
        title: String,
        message: String,
        hint: String,
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        canCancel: Boolean = true,
        validation: (String) -> Int? = { null },
        onResult: (String?) -> Unit
) {
    var et: EditText? = null
    standardDialog(title, message, canCancel, listOf(
            resources.getString(android.R.string.cancel)!! to { it: VCStack ->
                onResult(null)
                it.pop()
            },
            resources.getString(android.R.string.ok)!! to { it: VCStack ->
                if (et != null) {
                    val result = et!!.text.toString()
                    val error = validation(result)
                    if (error == null) {
                        onResult(result)
                        it.pop()
                    } else {
                        snackbar(error)
                    }
                }
            }
    ), {
        et = editText() {
            this.inputType = inputType
            this.hint = hint
        }
        et!!
    })
}