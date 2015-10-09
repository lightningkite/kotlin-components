package com.lightningkite.kotlincomponents.viewcontroller

import android.os.Bundle
import android.view.View

/**
 * Created by jivie on 6/26/15.
 */
abstract class ViewControllerActivity(id: String) : IntentSenderActivity() {

    abstract val startViewController: ViewController

    val frame: FrameViewControllerStack = FrameViewControllerStack(ViewControllerStack.get(id) { startViewController }, this)
    var view: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBeforeCreate()
        view = frame.make(this, ViewControllerStack.dummy)
        setContentView(view)
    }

    open fun setupBeforeCreate() {
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