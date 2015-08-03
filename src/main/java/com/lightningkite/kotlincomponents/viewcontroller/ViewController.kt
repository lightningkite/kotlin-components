package com.lightningkite.kotlincomponents.viewcontroller;

import android.content.Context
import android.view.View

/**
 * Created by jivie on 6/26/15.
 */
public interface ViewController {
    public fun make(context: Context, stack: ViewControllerStack): View
    public fun canLeave(): Boolean = true
    public val result: Any?
    public fun dispose(view: View)
}
