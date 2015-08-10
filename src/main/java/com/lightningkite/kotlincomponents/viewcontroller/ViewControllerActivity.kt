package com.lightningkite.kotlincomponents.viewcontroller

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lightningkite.kotlincomponents.animation.AnimationSet
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.layoutParams
import java.util.Stack

/**
 * Created by jivie on 6/26/15.
 */
public abstract class ViewControllerActivity : Activity(), ViewControllerStack {
    public companion object {
        public val stack: Stack<ViewControllerData> = Stack()
    }
    public var currentView: View? = null
    public var frame: FrameLayout? = null

    public val animationSetPush: AnimationSet? = null
    public val animationSetPop: AnimationSet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)
        frame = frameLayout {}
        if (stack.isEmpty()) onFirstCreate()
        else {
            switchView(null, stack.peek(), null);
        }
    }

    public abstract fun onFirstCreate()

    override fun pushView(newController: ViewController, onResult: (result: Any?) -> Unit) {
        val oldController = if (stack.size() > 0) stack.peek() else null
        stack.push(ViewControllerData(newController, onResult))

        switchView(oldController, newController, animationSetPush)
    }

    override fun popView() {
        if (stack.size() <= 1) return
        if (!stack.peek().canPop()) return
        val oldController = stack.pop()
        if (currentView != null) {
            oldController.dispose(currentView!!)
        }
        val newController = stack.last()
        (oldController.onResult)(oldController.result);
        switchView(oldController, newController, animationSetPop)
    }

    override fun resetView(newController: ViewController) {
        val oldController = stack.pop()
        if (currentView != null) {
            oldController.dispose(currentView!!)
        }
        stack.clear()
        stack.push(ViewControllerData(newController, {}))
        switchView(oldController, newController, animationSetPop)
    }

    override fun replaceView(newController: ViewController) {
        val oldController = stack.pop().controller
        stack.push(ViewControllerData(newController, {}))
        switchView(oldController, newController, animationSetPush)
    }

    protected fun switchView(
            oldController: ViewController?,
            newController: ViewController,
            animationSet: AnimationSet?) {

        val newView = newController.make(this, this)
        newView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        if (currentView != null && oldController != null) {
            val oldView = currentView!!
            if (animationSet == null) {
                frame?.removeView(oldView)
                oldController.dispose(oldView)
            } else {
                oldView.(animationSet.animateOut)(frame!!).withEndAction {
                    frame?.removeView(oldView)
                    oldController.dispose(oldView)
                }.start()
                newView.(animationSet.animateIn)(frame!!).start()
            }
        }

        frame?.addView(newView)
        currentView = newView
    }

    override fun onBackPressed() {
        if (stack.size() > 1) {
            popView()
        } else {
            super<Activity>.onBackPressed()
        }
    }

    override fun onDestroy() {
        super<Activity>.onDestroy()
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