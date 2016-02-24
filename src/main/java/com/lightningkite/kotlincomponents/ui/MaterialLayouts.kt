package com.lightningkite.kotlincomponents.ui

import android.R
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.lightningkite.kotlincomponents.linearLayout
import com.lightningkite.kotlincomponents.selectableItemBackgroundBorderlessResource
import com.lightningkite.kotlincomponents.textColorResource
import com.lightningkite.kotlincomponents.verticalLayout
import org.jetbrains.anko.*

/**
 * Created by jivie on 2/15/16.
 */

inline fun TextView.materialStyleTertiary(dark: Boolean) {
    textColorResource = if (dark) R.color.tertiary_text_dark else R.color.tertiary_text_light
    textSize = 16f
    ellipsize = TextUtils.TruncateAt.END
}

inline fun TextView.materialStylePrimary(dark: Boolean) {
    textColorResource = if (dark) R.color.primary_text_dark else R.color.primary_text_light
    textSize = 16f
    ellipsize = TextUtils.TruncateAt.END
}

inline fun ImageButton.materialStyleAction() {
    leftPadding = dip(16)
    backgroundResource = selectableItemBackgroundBorderlessResource
}

inline fun rowOneLine(
        context: Context,
        dark: Boolean = false,
        crossinline title: TextView.() -> Unit
): LinearLayout {
    return verticalLayout(context) {
        minimumHeight = dip(72)
        padding = dip(16)
        gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
        textView() {
            materialStylePrimary(dark)
            title()
        }
    }
}

inline fun rowOneLineRadio(
        context: Context,
        dark: Boolean = false,
        crossinline title: TextView.() -> Unit,
        crossinline radioButtonInit: RadioButton.() -> Unit
): LinearLayout {
    return linearLayout(context) {
        minimumHeight = dip(72)
        padding = dip(16)
        gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
        val rad = radioButton() {
            radioButtonInit()
        }.lparams(wrapContent, wrapContent) {
            rightMargin = dip(16)
        }
        textView() {
            materialStylePrimary(dark)
            title()
        }.lparams(0, wrapContent, 1f)

        onClick() {
            rad.performClick()
        }
    }
}

inline fun rowTwoLine(
        context: Context,
        dark: Boolean = false,
        crossinline title: TextView.() -> Unit,
        crossinline subtitle: TextView.() -> Unit
): LinearLayout {
    return verticalLayout(context) {
        minimumHeight = dip(72)
        padding = dip(16)
        gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
        textView() {
            materialStylePrimary(dark)
            title()
        }.lparams { bottomMargin = dip(4) }
        textView() {
            materialStyleTertiary(dark)
            subtitle()
        }
    }
}

inline fun rowTwoLineAction(
        context: Context,
        actionIcon: Int,
        crossinline action: () -> Unit,
        dark: Boolean = false,
        crossinline title: TextView.() -> Unit,
        crossinline subtitle: TextView.() -> Unit
): LinearLayout {
    return linearLayout(context) {
        minimumHeight = dip(72)
        padding = dip(16)
        gravity = Gravity.CENTER
        verticalLayout {
            textView() {
                materialStylePrimary(dark)
                title()
            }.lparams { bottomMargin = dip(4) }
            textView() {
                materialStyleTertiary(dark)
                subtitle()
            }
        }.lparams(0, wrapContent, 1f)

        imageButton(actionIcon) {
            materialStyleAction()
            onClick {
                action()
            }
        }
    }
}

inline fun rowTwoTwoLine(
        context: Context,
        dark: Boolean = false,
        crossinline title: TextView.() -> Unit,
        crossinline subtitle: TextView.() -> Unit,
        crossinline rightTop: TextView.() -> Unit,
        crossinline rightBottom: TextView.() -> Unit
): LinearLayout {
    return linearLayout(context) {
        minimumHeight = dip(72)
        padding = dip(16)
        gravity = Gravity.CENTER
        verticalLayout {
            textView() {
                materialStylePrimary(dark)
                title()
            }.lparams { bottomMargin = dip(4) }
            textView() {
                materialStyleTertiary(dark)
                subtitle()
            }
        }.lparams(0, wrapContent, 1f)

        verticalLayout {
            gravity = Gravity.RIGHT
            textView() {
                materialStyleTertiary(dark)
                rightTop()
            }.lparams(wrapContent, wrapContent) { bottomMargin = dip(4) }
            textView() {
                materialStyleTertiary(dark)
                rightBottom()
            }.lparams(wrapContent, wrapContent)
        }.lparams(wrapContent, wrapContent)
    }
}

inline fun rowTwoTwoLineAction(
        context: Context,
        actionIcon: Int,
        crossinline action: () -> Unit,
        dark: Boolean = false,
        crossinline title: TextView.() -> Unit,
        crossinline subtitle: TextView.() -> Unit,
        crossinline rightTop: TextView.() -> Unit,
        crossinline rightBottom: TextView.() -> Unit
): LinearLayout {
    return linearLayout(context) {
        minimumHeight = dip(72)
        padding = dip(16)
        gravity = Gravity.CENTER
        verticalLayout {
            textView() {
                materialStylePrimary(dark)
                title()
            }.lparams { bottomMargin = dip(4) }
            textView() {
                materialStyleTertiary(dark)
                subtitle()
            }
        }.lparams(0, wrapContent, 1f)

        verticalLayout {
            gravity = Gravity.RIGHT
            textView() {
                materialStyleTertiary(dark)
                rightTop()
            }.lparams(wrapContent, wrapContent) { bottomMargin = dip(4) }
            textView() {
                materialStyleTertiary(dark)
                rightBottom()
            }.lparams(wrapContent, wrapContent)
        }.lparams(wrapContent, wrapContent)

        imageButton(actionIcon) {
            materialStyleAction()
            onClick {
                action()
            }
        }
    }
}