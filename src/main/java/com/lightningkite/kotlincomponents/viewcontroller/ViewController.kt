package com.lightningkite.kotlincomponents.viewcontroller;

import android.content.Context
import android.content.res.Resources
import android.view.View

/**
 * Created by jivie on 6/26/15.
 */
public interface ViewController {
    public fun make(context: Context, stack: ViewControllerStack): View
    public fun canPop(): Boolean = true
    public val result: Any? get() = null
    public fun dispose(view: View)
    public fun getTitle(resources: Resources): String = "Untitled"
}
