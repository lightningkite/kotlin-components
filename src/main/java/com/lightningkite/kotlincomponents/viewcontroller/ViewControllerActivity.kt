package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.layoutParams
import java.util.Stack

/**
 * Created by jivie on 6/26/15.
 */
public abstract class ViewControllerActivity : LifecycleActivity(), ViewControllerStack {
    public companion object {
        public val stack: Stack<ViewControllerData> = Stack()
    }
    public var currentView: View? = null
    public var frame: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<LifecycleActivity>.onCreate(savedInstanceState)
        frame = frameLayout {}
        if (stack.isEmpty()) onFirstCreate()
        else {
            switchView(null, stack.peek());
        }
    }

    public abstract fun onFirstCreate()

    override fun pushView(newController: ViewController, onResult: (result: Any?) -> Unit) {
        val oldController = if (stack.size() > 0) stack.peek() else null
        stack.push(ViewControllerData(newController, onResult))

        switchView(oldController, newController)//, popOutTransition, popInTransition)
    }

    override fun popView() {
        if (stack.size() <= 1) return
        val oldController = stack.pop()
        if (currentView != null) {
            oldController.dispose(currentView!!)
        }
        val newController = stack.last()
        (oldController.onResult)(oldController.result);
        switchView(oldController, newController)//, popOutTransition, popInTransition)
    }

    protected fun switchView(
            oldController: ViewController?,
            newController: ViewController) {

        val newView = newController.make(this, this)
        newView.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        if (currentView != null && oldController != null) {
            frame?.removeView(currentView)
            oldController.dispose(currentView!!)
        }

        frame?.addView(newView)
        currentView = newView
    }

    override fun onBackPressed() {
        if (stack.size() > 1 && stack.peek().canLeave()) {
            popView()
        } else {
            super<LifecycleActivity>.onBackPressed()
        }
    }

    override fun onDestroy() {
        super<LifecycleActivity>.onDestroy()
        if (currentView != null) {
            stack.last().dispose(currentView!!)
        }
    }

    private var onResultLambda: (result: Int, data: Intent?) -> Unit = { result, data -> }
    override fun startIntent(intent: Intent, onResult: (result: Int, data: Intent?) -> Unit, options: Bundle) {
        onResultLambda = onResult
        startActivityForResult(intent, 0, options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onResultLambda(resultCode, data)
    }
}