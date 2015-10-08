package com.lightningkite.kotlincomponents.viewcontroller

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.logging.logD
import java.util.*

/**
 * Created by jivie on 7/24/15.
 */
public open class ViewControllerView(activity: Activity,
                                startVC: ViewController,
                                intentListener: ViewControllerView.IntentListener,
                                tag: String = "default",
                                animationSetPush: AnimationSet? = null,
                                animationSetPop: AnimationSet? = null
) : FrameLayout(activity), ViewControllerStack {

    public companion object {
        public val stacks: HashMap<String, Stack<ViewControllerData>> = HashMap()
        public fun getStack(tag: String): Stack<ViewControllerData> {
            if (stacks.containsKey(tag)) return stacks.get(tag)!!
            val newStack = Stack<ViewControllerData>()
            stacks.put(tag, newStack)
            return newStack
        }
        private var onResultLambda: (result: Int, data: Intent?) -> Unit = { result, data -> }
    }

    public val activity: Activity = activity
    public var currentView: View? = null
    public val tag: String = tag;
    public val intentListener: IntentListener = intentListener
    public var stack: Stack<ViewControllerData> = getStack(tag)
    public var onStackChange: (ViewControllerView) -> Unit = {}

    public var animationSetPush: AnimationSet? = animationSetPush
    public var animationSetPop: AnimationSet? = animationSetPop

    init {
        logD(stack.size())
        if (stack.isEmpty()) {
            pushView(startVC)
        } else {
            switchView(null, stack.peek(), null);
        }
    }

    override fun pushView(newController: ViewController, onResult: (result: Any?) -> Unit) {
        val oldController = if (stack.size() > 0) stack.peek() else null
        stack.push(ViewControllerData(newController, onResult))

        switchView(oldController, newController, animationSetPush)

        onStackChange(this)
    }

    override fun popView(): Boolean {
        if (stack.size() <= 1) return false
        val oldController = stack.pop()
        val newController = stack.last()
        switchView(oldController, newController, animationSetPop)
        (oldController.onResult)(oldController.result);
        oldController.dispose()
        onStackChange(this)
        return true
    }

    override fun resetView(newController: ViewController) {
        var oldController: ViewController? = null
        if (stack.size() >= 1) {
            oldController = stack.pop()
        }
        while (stack.isNotEmpty()) {
            stack.pop().dispose()
        }
        stack.push(ViewControllerData(newController, {}))
        switchView(oldController, newController, animationSetPop)

        onStackChange(this)
    }

    override fun replaceView(newController: ViewController) {
        if (stack.size() < 1) return
        val oldController = stack.pop().controller
        stack.push(ViewControllerData(newController, {}))
        switchView(oldController, newController, animationSetPush)
        oldController.dispose()
        onStackChange(this)
    }

    protected fun switchView(
            oldController: ViewController?,
            newController: ViewController,
            animationSet: AnimationSet?) {

        val newView = newController.make(activity, this)
        newView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        if (currentView != null && oldController != null) {
            val oldView = currentView!!
            if (animationSet == null) {
                removeView(oldView)
                oldController.unmake(oldView)
            } else {
                oldView.(animationSet.animateOut)(this).withEndAction {
                    this.removeView(oldView)
                    oldController.unmake(oldView)
                }.start()
                newView.(animationSet.animateIn)(this).start()
            }
        }

        addView(newView)
        currentView = newView
    }

    override fun startIntent(intent: Intent, onResult: (result: Int, data: Intent?) -> Unit, options: Bundle) {
        onResultLambda = onResult
        intentListener.startActivityForResult(tag, intent, options)
    }

    //Users of the class are responsible for calling this!
    public fun onActivityResult(resultCode: Int, data: Intent?) {
        onResultLambda(resultCode, data)
    }

    public fun dispose() {
        if (currentView != null) {
            stack.lastOrNull()?.unmake(currentView!!)
        }
        for(vc in stack){
            vc.dispose()
        }
    }


    interface IntentListener {
        public fun startActivityForResult(tag: String, intent: Intent, options: Bundle)
    }
}

