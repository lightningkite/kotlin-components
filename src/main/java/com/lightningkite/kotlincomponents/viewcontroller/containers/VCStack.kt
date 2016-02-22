package com.lightningkite.kotlincomponents.viewcontroller.containers

import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import java.util.*

/**
 * A stack of [ViewController]s.  You can [push] and [pop] them, among other things.
 * Created by jivie on 10/12/15.
 */
open class VCStack() : VCContainerImpl() {
    override val current: ViewController get() = stack.peek()

    val size: Int get() = stack.size
    val isEmpty: Boolean get() = stack.isEmpty()
    var onEmptyListener: () -> Unit = {}


    private val stack: Stack<ViewController> = Stack()


    fun push(viewController: ViewController, animationSet: AnimationSet? = AnimationSet.slidePush) {
        stack.push(viewController)
        swapListener?.invoke(current, animationSet) {}
        onSwap.forEach { it(current) }
    }

    fun pop(animationSet: AnimationSet? = AnimationSet.slidePop) {
        if (stack.size <= 1) {
            onEmptyListener()
        } else {
            val toDispose = stack.pop()
            swapListener?.invoke(current, animationSet) {
                toDispose.dispose()
            }
            onSwap.forEach { it(current) }
        }
    }

    fun back(predicate: (ViewController) -> Boolean, animationSet: AnimationSet? = AnimationSet.slidePop) {
        val toDispose = ArrayList<ViewController>()
        while (!predicate(stack.peek())) {
            toDispose.add(stack.pop())
            if (stack.size == 0) throw IllegalArgumentException("There is no view controller that matches this predicate!")
        }
        swapListener?.invoke(current, animationSet) {
            toDispose.forEach {
                it.dispose()
            }
        }
        onSwap.forEach { it(current) }
    }

    fun swap(viewController: ViewController, animationSet: AnimationSet? = null) {
        val toDispose = stack.pop()
        stack.push(viewController)
        swapListener?.invoke(current, animationSet) {
            toDispose.dispose()
        }
        onSwap.forEach { it(current) }
    }

    fun reset(viewController: ViewController, animationSet: AnimationSet? = null) {
        val toDispose = ArrayList(stack)
        stack.clear()
        stack.push(viewController)
        swapListener?.invoke(current, animationSet) {
            toDispose.forEach { it.dispose() }
        }
        onSwap.forEach { it(current) }
    }

    override fun onBackPressed(backAction: () -> Unit) {
        if (stack.size == 0) {
            backAction()
        } else if (stack.size == 1) {
            current.onBackPressed {
                backAction()
            }
        } else {
            current.onBackPressed {
                pop()
            }
        }
    }

    override fun dispose() {
        for (vc in stack) {
            vc.dispose()
        }
    }
}