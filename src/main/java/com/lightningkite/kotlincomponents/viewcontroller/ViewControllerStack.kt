package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lightningkite.kotlincomponents.animation.AnimationSet
import java.util.*

public interface ViewControllerStack {

    public val intentSender: IntentSender
    public val stack: Stack<ViewControllerData>
    public val defaultAnimationSetPush: AnimationSet? get() = AnimationSet.slidePush
    public val defaultAnimationSetPop: AnimationSet? get() = AnimationSet.slidePop

    public fun swap(
            newViewController: ViewController,
            animationSet: AnimationSet? = null
    )

    public fun startIntent(intent: Intent, onResult: (result: Int, data: Intent?) -> Unit = { resultCode, data -> }) {
        startIntent(intent, onResult, Bundle.EMPTY)
    }

    public fun startIntent(intent: Intent, onResult: (result: Int, data: Intent?) -> Unit, options: Bundle)
            = intentSender.startIntent(intent, onResult, options)

    public fun pushView(newController: ViewController, animationSet: AnimationSet? = null, onResult: (Any?) -> Unit) {
        stack.push(ViewControllerData(newController, onResult))
        swap(newController, animationSet ?: defaultAnimationSetPush)
    }

    public fun pushView(newController: ViewController, animationSet: AnimationSet? = null): Unit
            = pushView(newController, animationSet, {})

    public fun popView(animationSet: AnimationSet? = null): Boolean {
        if (stack.size() == 1) return false
        val result = stack.pop().result
        val newController = stack.peek()
        swap(newController, animationSet ?: defaultAnimationSetPop)
        newController.onResult(result)
        return true
    }

    public fun resetView(newController: ViewController, animationSet: AnimationSet? = null) {
        stack.clear()
        stack.push(ViewControllerData(newController))
        swap(newController, animationSet ?: defaultAnimationSetPush)
    }

    public fun replaceView(newController: ViewController, animationSet: AnimationSet? = null) {
        stack.pop()
        stack.push(ViewControllerData(newController))
        swap(newController, animationSet ?: defaultAnimationSetPush)
    }

    public open class DummyStack : ViewControllerStack {
        override val intentSender: IntentSender = object : IntentSender {
            override fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit, options: Bundle) {
            }
        }
        override val stack: Stack<ViewControllerData> = Stack()

        init {
            stack.add(ViewControllerData(object : ViewController {
                override fun make(context: Context, stack: ViewControllerStack): View {
                    return View(context)
                }
            }))
        }

        override fun swap(newViewController: ViewController, animationSet: AnimationSet?) {
        }
    }

    public companion object {

        public val stacks: HashMap<String, Stack<ViewControllerData>> = HashMap()
        fun get(name: String, default: () -> ViewController): Stack<ViewControllerData> {
            return stacks.get(name) ?: Stack<ViewControllerData>().apply {
                add(ViewControllerData(default()))
            }
        }

        public val dummy: ViewControllerStack = DummyStack()
    }
}

public data class ViewControllerData(val controller: ViewController, var onResult: (result: Any?) -> Unit = {}) : ViewController by controller