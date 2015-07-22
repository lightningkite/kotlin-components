package com.lightningkite.kotlincomponents.layview;

import android.content.Context
import android.os.Parcelable
import android.view.View

/**
 * Created by jivie on 6/26/15.
 */
public interface ViewController {
    //REQUIREMENT: All view controllers MUST have an empty constructor!
    public fun make(context: Context): View

    public fun loadState(state: Parcelable)
    public fun saveState(): Parcelable?
    public fun dispose(view: View)
}
