package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.logging.logD
import java.util.*
import kotlin.reflect.KClass

public interface ViewControllerStack {

    public val intentSender: IntentSender
    public val stack: Stack<ViewControllerData>
    public val defaultAnimationSetPush: AnimationSet? get() = AnimationSet.slidePush
    public val defaultAnimationSetPop: AnimationSet? get() = AnimationSet.slidePop

    public fun onAnimationComplete(action: () -> Unit) = action()

    public fun logStack() {
        val builder = StringBuilder()
        for (data in stack) {
            builder.append(data.controller.javaClass.simpleName)
            builder.append(" ")
        }
        logD("(" + builder.toString() + ")")
    }

    public fun swap(
            newViewController: ViewController,
            animationSet: AnimationSet? = null
    )

    public fun onStackEmptied()

    public fun startIntent(intent: Intent, onResult: (result: Int, data: Intent?) -> Unit = { resultCode, data -> }) {
        startIntent(intent, onResult, Bundle.EMPTY)
    }

    public fun startIntent(intent: Intent, onResult: (result: Int, data: Intent?) -> Unit, options: Bundle)
            = intentSender.startIntent(intent, onResult, options)

    public fun pushView(newController: ViewController, animationSet: AnimationSet? = null, onResult: (Any?) -> Unit) {
        stack.peek().onResult = onResult
        stack.push(ViewControllerData(newController))
        swap(newController, animationSet ?: defaultAnimationSetPush)
    }

    public fun pushView(newController: ViewController, animationSet: AnimationSet? = null): Unit
            = pushView(newController, animationSet, {})

    public fun popViewForce(result: Any? = null, animationSet: AnimationSet? = null) {
        if (stack.size() <= 1) onStackEmptied()
        val oldController = stack.pop()
        val newController = stack.peek()
        swap(newController, animationSet ?: defaultAnimationSetPop)
        newController.onResult(result)
    }

    public fun popView(result: Any? = null, animationSet: AnimationSet? = null): Boolean{
        return stack.peek().finish(this)
    }

    public fun resetView(result: Any? = null, newController: ViewController, animationSet: AnimationSet? = null) {
        val oldController = stack.pop()
        stack.clear()
        stack.push(ViewControllerData(newController))
        swap(newController, animationSet ?: defaultAnimationSetPush)
    }

    public fun backToView(predicate: (ViewController) -> Boolean, result: Any? = null, animationSet: AnimationSet? = null) {
        val oldController = stack.peek()
        while (!predicate(stack.peek().controller)) {
            stack.pop()
            //TODO: Custom error here when predicate fails.
        }
        val newController = stack.peek()
        swap(newController, animationSet ?: defaultAnimationSetPop)
        newController.onResult(result)
    }

    public fun backToView(ofType: Class<*>, result: Any? = null, animationSet: AnimationSet? = null)
            = backToView({ it.javaClass == ofType }, result, animationSet)

    public fun backToView(ofType: KClass<*>, result: Any? = null, animationSet: AnimationSet? = null)
            = backToView(ofType.java, result, animationSet)

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
        override val stack: Stack<ViewControllerData> = object : Stack<ViewControllerData>() {
            override fun push(`object`: ViewControllerData?): ViewControllerData? {
                return null
            }

            override fun pop(): ViewControllerData? {
                return null
            }
        }

        init {
            stack.add(ViewControllerData(object : ViewController {
                override fun make(context: Context, stack: ViewControllerStack): View {
                    return View(context)
                }
            }))
        }

        override fun backToView(predicate: (ViewController) -> Boolean, result: Any?, animationSet: AnimationSet?) {

        }

        override fun swap(newViewController: ViewController, animationSet: AnimationSet?) {
        }
    }

    public companion object {

        public val stacks: HashMap<String, Stack<ViewControllerData>> = HashMap()
        fun get(name: String, default: () -> ViewController): Stack<ViewControllerData> {
            return stacks.get(name) ?: Stack<ViewControllerData>().apply {
                add(ViewControllerData(default()))
                stacks.put(name, this)
            }
        }

        public val dummy: ViewControllerStack = DummyStack()
    }
}

public data class ViewControllerData(val controller: ViewController, var onResult: (result: Any?) -> Unit = {}) : ViewController by controller