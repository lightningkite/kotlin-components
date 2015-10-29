package com.lightningkite.kotlincomponents.viewcontroller;

import android.content.Context
import android.content.res.Resources
import android.view.View
import com.lightningkite.kotlincomponents.Disposable
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity

/**
 * Created by jivie on 6/26/15.
 */
public interface ViewController : Disposable {
    object EMPTY : ViewController {
        override fun make(activity: VCActivity): View = View(activity)
    }

    public fun make(activity: VCActivity): View
    public fun unmake(view: View) {
    }
    override fun dispose() {
    }
    public fun getTitle(resources: Resources): String = "Untitled"

    public fun onBackPressed(backAction:()->Unit){backAction()}
}
