package com.lightningkite.kotlincomponents.animation

import android.view.View
import android.view.ViewGroup

/**
 * Various functions to assist with animating things.
 */

/**
 * Creates a resizer function.
 */
fun View.animateHeightUpdate(duration: Long, layoutParamsRef: ViewGroup.LayoutParams, startSize: Float? = null): () -> Unit {

    val heightAnimator = ActionAnimator(this, startSize, {
        layoutParamsRef.height = it.toInt()
        requestLayout()
    }, Interpolate.float)

    return {
        this.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
        )
        val newHeight = measuredHeight
        heightAnimator.animate(newHeight.toFloat(), duration)
    }
}

/**
 * Creates a resizer function.
 */
fun View.animateWidthUpdate(duration: Long, layoutParamsRef: ViewGroup.LayoutParams, startSize: Float? = null): () -> Unit {

    val widthAnimator = ActionAnimator(this, startSize, {
        layoutParamsRef.width = it.toInt()
        requestLayout()
    }, Interpolate.float)

    return {
        this.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
        )
        val newWidth = measuredWidth
        widthAnimator.animate(newWidth.toFloat(), duration)
    }
}