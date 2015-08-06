package com.lightningkite.kotlincomponents.viewcontroller

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import org.jetbrains.anko.layoutParams
import java.util.HashMap
import java.util.Stack

/**
 * Created by jivie on 7/24/15.
 */
public class ViewControllerView(activity: Activity, startVC: ViewController, intentListener: ViewControllerView.IntentListener, tag: String = "default") : FrameLayout(activity), ViewControllerStack {

    public companion object {
        public val stacks: HashMap<String, Stack<ViewControllerData>> = HashMap()
        public fun getStack(tag: String): Stack<ViewControllerData> {
            if (stacks.containsKey(tag)) return stacks.get(tag)
            val newStack = Stack<ViewControllerData>()
            stacks.put(tag, newStack)
            return newStack
        }
    }

    public val activity: Activity = activity
    public var currentView: View? = null
    public val tag: String = tag;
    public val intentListener: IntentListener = intentListener
    public var stack: Stack<ViewControllerData> = getStack(tag)
    public var onStackChange: (ViewControllerView) -> Unit = {}

    init {
        if (stack.isEmpty()) {
            pushView(startVC)
        } else {
            switchView(null, stack.peek());
        }
    }

    override fun pushView(newController: ViewController, onResult: (result: Any?) -> Unit) {
        val oldController = if (stack.size() > 0) stack.peek() else null
        stack.push(ViewControllerData(newController, onResult))

        switchView(oldController, newController)//, popOutTransition, popInTransition)

        onStackChange(this)
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

        onStackChange(this)
    }

    override fun resetView(newController: ViewController) {
        var oldController: ViewController? = null
        if (stack.size() >= 1) {
            oldController = stack.pop()
            if (currentView != null) {
                oldController.dispose(currentView!!)
            }
        }
        stack.clear()
        stack.push(ViewControllerData(newController, {}))
        switchView(oldController, newController)

        onStackChange(this)
    }

    override fun replaceView(newController: ViewController) {
        if (stack.size() < 1) return
        val oldController = stack.pop().controller
        stack.push(ViewControllerData(newController, {}))
        switchView(oldController, newController)

        onStackChange(this)
    }

    protected fun switchView(
            oldController: ViewController?,
            newController: ViewController) {

        val newView = newController.make(activity, this)
        newView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        if (currentView != null && oldController != null) {
            removeView(currentView)
            oldController.dispose(currentView!!)
        }

        addView(newView)
        currentView = newView
    }

    private var onResultLambda: (result: Int, data: Intent?) -> Unit = { result, data -> }
    override fun startIntent(intent: Intent, onResult: (result: Int, data: Intent?) -> Unit, options: Bundle) {
        onResultLambda = onResult
        intentListener.startActivityForResult(tag, intent, options)
    }

    //Users of the class are responsible for calling this!
    public fun onActivityResult(resultCode: Int, data: Intent?) {
        onResultLambda(resultCode, data)
    }


    interface IntentListener {
        public fun startActivityForResult(tag: String, intent: Intent, options: Bundle)
    }
}

