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
    public val stack: Stack<ViewController> = Stack()
    public var currentView: View? = null
    public var frame: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<LifecycleActivity>.onCreate(savedInstanceState)
        frame = frameLayout {}
        if(savedInstanceState != null) loadInstanceState(savedInstanceState)
        else onFirstCreate()
    }

    public abstract fun onFirstCreate()

    public fun loadInstanceState(savedInstanceState: Bundle){
        stack.loadState(savedInstanceState, "stack")
        currentView = stack.peek().make(this, this)
        frame?.addView(currentView)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<LifecycleActivity>.onSaveInstanceState(outState)
        stack.saveState(outState, "stack")
    }

    override fun pushView(newController: ViewController) {
        val oldController = if (stack.size() > 0) stack.peek() else null
        stack.push(newController)

        switchView(oldController, newController)//, popOutTransition, popInTransition)
    }

    override fun popView() {
        if (stack.size() <= 1) return
        val oldController = stack.pop()
        if (currentView != null) {
            oldController.dispose(currentView!!)
        }
        val newController = stack.last()

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
        if (stack.size() > 1) {
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

    override fun optView(tag: String): ViewController? {
        var returnVal: ViewController? = null
        for (controller in stack) {
            if (controller.tag.equals(tag)) returnVal = controller
        }
        return returnVal
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