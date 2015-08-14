package com.lightningkite.kotlincomponents

import android.R
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import org.jetbrains.anko.*

/**
 * Created by jivie on 7/16/15.
 */
public inline fun ViewController.makeLinearLayout(context: Context, init: _LinearLayout.() -> Unit): LinearLayout {
    val layout = _LinearLayout(context);
    layout.orientation = LinearLayout.VERTICAL
    layout.init();
    return layout;
}

public inline fun ViewController.makeFrameLayout(context: Context, init: _FrameLayout.() -> Unit): FrameLayout {
    val layout = _FrameLayout(context);
    layout.init();
    return layout;
}

public inline fun ViewController.makeRelativeLayout(context: Context, init: _RelativeLayout.() -> Unit): RelativeLayout {
    val layout = _RelativeLayout(context);
    layout.init();
    return layout;
}

public fun ViewController.makeScrollView(context: Context, content: View): ScrollView {
    val layout = ScrollView(context)
    layout.addView(content)
    return layout
}

public fun ViewController.inflate(context: Context, LayoutRes layoutResource: Int, init: View.() -> Unit): View {
    val layout = LayoutInflater.from(context).inflate(layoutResource, null);
    layout.init();
    return layout;
}

public fun View.dip(value: Int): Int = getContext().dip(value)
public val View.context: Context get() = getContext()

public fun View.animateHighlight(milliseconds: Long, color: Int, millisecondsTransition: Int = 200) {
    assert(milliseconds > millisecondsTransition * 2, "The time shown must be at least twice as much as the transition time")
    val originalBackground = getBackground()
    val transition = TransitionDrawable(arrayOf(originalBackground, ColorDrawable(color)))
    transition.setCrossFadeEnabled(false)
    setBackground(transition)
    transition.startTransition(millisecondsTransition)
    postDelayed(milliseconds - millisecondsTransition) {
        transition.reverseTransition(millisecondsTransition)
        postDelayed(millisecondsTransition.toLong()) {
            setBackground(originalBackground)
        }
    }
}

public fun ViewGroup.MarginLayoutParams.setMarginsDip(context: Context, left: Int, top: Int, right: Int, bottom: Int) {
    setMargins(context.dip(left), context.dip(top), context.dip(right), context.dip(bottom))
}

public fun View.setLayoutParamsMargin(context: Context, width: Int, height: Int, left: Int, top: Int, right: Int, bottom: Int) {
    val params = ViewGroup.MarginLayoutParams(
            if (width != matchParent && width != wrapContent && width != 0)
                context.dip(width)
            else
                width,
            if (height != matchParent && height != wrapContent && height != 0)
                context.dip(height)
            else
                height
    )
    params.setMargins(context.dip(left), context.dip(top), context.dip(right), context.dip(bottom))
    layoutParams = params
}

public DrawableRes val View.selectableItemBackground: Int
    get() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            val outValue = TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.selectableItemBackground, outValue, true);
            return outValue.resourceId
        }
        return 0
    }

public fun View.getActivity(): Activity? {
    return getContext().getActivity()
}

public fun Context.getActivity(): Activity? {
    if (this is Activity) {
        return this
    } else if (this is ContextWrapper) {
        return getBaseContext().getActivity()
    } else {
        return null
    }
}

public fun View.postDelayed(milliseconds: Long, action: () -> Unit) {
    postDelayed(object : Runnable {
        override fun run() = action()
    }, milliseconds)
}

public inline fun <T : View> ViewGroup.add(view: T, setup: T.() -> Unit): T {
    view.setup();
    addView(view)
    return view
}

public inline fun <reified T : View> ViewGroup.add(setup: T.() -> Unit): T {
    val view = javaClass<T>().getConstructor(javaClass<Context>()).newInstance(getContext())
    view.setup();
    addView(view)
    return view
}

private val cachedPoint: Point = Point()
public val View.screenSize: Point get() {
    getContext().windowManager.getDefaultDisplay().getSize(cachedPoint)
    return cachedPoint
}
public val View.parentView: View get() {
    return getParent() as? View ?: throw IllegalStateException("Parent is not a ViewGroup!")
}