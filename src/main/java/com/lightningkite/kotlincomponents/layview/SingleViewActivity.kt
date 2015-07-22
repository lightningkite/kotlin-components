package com.lightningkite.kotlincomponents.layview

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.layoutParams

/**
 * Created by jivie on 6/26/15.
 */
public abstract class SingleViewActivity : LifecycleActivity() {
    public val stack: ViewControllerStack = ViewControllerStack()
    public var currentView: View? = null
    public var frame: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        frame = frameLayout {}
        if(savedInstanceState != null) loadInstanceState(savedInstanceState)
        else onFirstCreate()
    }

    public abstract fun onFirstCreate()

    public fun loadInstanceState(savedInstanceState: Bundle){
        stack.loadState(savedInstanceState, "stack")
        currentView = stack.top.make(this)
        frame?.addView(currentView)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stack.saveState(outState, "stack")
    }

    public fun pushView(newController: ViewController) {
        val oldController = if (stack.size() > 0) stack.peek() else null
        stack.push(newController)

        switchView(oldController, newController)//, popOutTransition, popInTransition)
    }

    public fun popView() {
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

        val newView = newController.make(this)
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
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (currentView != null) {
            stack.last().dispose(currentView!!)
        }
    }
}