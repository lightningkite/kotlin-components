package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Context
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import org.jetbrains.anko.orientation

/**
 * Created by jivie on 7/16/15.
 */
public inline fun ViewController.makeLinearLayout(context: Context, init: LinearLayout.() -> Unit): LinearLayout {
    val layout = LinearLayout(context);
    layout.orientation = LinearLayout.VERTICAL
    layout.init();
    return layout;
}

public inline fun ViewController.makeFrameLayout(context: Context, init: FrameLayout.() -> Unit): FrameLayout {
    val layout = FrameLayout(context);
    layout.init();
    return layout;
}

public inline fun ViewController.makeRelativeLayout(context: Context, init: RelativeLayout.() -> Unit): RelativeLayout {
    val layout = RelativeLayout(context);
    layout.init();
    return layout;
}

public fun ViewController.inflate(context: Context, LayoutRes layoutResource: Int, init: View.() -> Unit): View {
    val layout = LayoutInflater.from(context).inflate(layoutResource, null);
    layout.init();
    return layout;
}

public DrawableRes val View.selectableItemBackground: Int
    get() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            val outValue = TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            return outValue.resourceId
        }
        return 0
}