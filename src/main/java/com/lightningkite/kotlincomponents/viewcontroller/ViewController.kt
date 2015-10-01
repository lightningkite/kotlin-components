package com.lightningkite.kotlincomponents.viewcontroller;

import android.content.Context
import android.content.res.Resources
import android.view.View
import com.lightningkite.kotlincomponents.Disposable

/**
 * Created by jivie on 6/26/15.
 */
public interface ViewController : Disposable {
    public fun make(context: Context, stack: ViewControllerStack): View
    public fun canPop(): Boolean = true
    public val result: Any? get() = null
    public fun unmake(view: View) {
    }

    override fun dispose() {
    }
    public fun getTitle(resources: Resources): String = "Untitled"
}
