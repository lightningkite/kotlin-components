package com.lightningkite.kotlincomponents

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.windowManager

/**
 * Created by jivie on 7/22/15.
 */
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