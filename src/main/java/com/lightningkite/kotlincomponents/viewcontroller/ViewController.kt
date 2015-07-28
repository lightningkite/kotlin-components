package com.lightningkite.kotlincomponents.viewcontroller;

import android.content.Context
import android.os.Parcelable
import android.view.View

/**
 * Created by jivie on 6/26/15.
 */
public interface ViewController {
    //REQUIREMENT: All view controllers MUST have an empty constructor!
    public val tag: String

    public fun make(context: Context, stack: ViewControllerStack): View
    public fun loadState(state: Parcelable)
    public fun saveState(): Parcelable?
    public fun canLeave(): Boolean = true
    public fun dispose(view: View)
}
