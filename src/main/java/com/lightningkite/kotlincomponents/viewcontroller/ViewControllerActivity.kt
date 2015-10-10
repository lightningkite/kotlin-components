package com.lightningkite.kotlincomponents.viewcontroller

import android.os.Bundle
import android.view.View
import com.lightningkite.kotlincomponents.logging.logD

/**
 * Created by jivie on 6/26/15.
 */
abstract class ViewControllerActivity() : IntentSenderActivity() {

    abstract val startViewController: ViewController
    abstract val tag: String

    lateinit var frame: FrameViewControllerStack
    var view: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBeforeCreate()
        val stack = ViewControllerStack.get(tag) { startViewController }
        logD(stack.size())
        frame = FrameViewControllerStack(stack, this)
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