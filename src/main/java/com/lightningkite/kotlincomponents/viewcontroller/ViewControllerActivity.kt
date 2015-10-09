package com.lightningkite.kotlincomponents.viewcontroller

import android.os.Bundle
import android.view.View

/**
 * Created by jivie on 6/26/15.
 */
abstract class ViewControllerActivity(id: String) : IntentSenderActivity() {

    abstract val defaultVC: ViewController

    val frame: FrameViewControllerStack = FrameViewControllerStack(ViewControllerStack.get(id) { defaultVC }, this)
    var view: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = frame.make(this, ViewControllerStack.dummy)
        setContentView(view)
    }

    override fun onBackPressed() {
        if (!frame.popView()) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (view != null) {
            frame.unmake(view!!)
        }
        frame.dispose()
        super.onDestroy()
    }
}