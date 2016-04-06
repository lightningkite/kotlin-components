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

    var defaultPushAnimation = AnimationSet.slidePush
    var defaultPopAnimation = AnimationSet.slidePop
    var defaultSwapAnimation = AnimationSet.fade

    val size: Int get() = stack.size
    val isEmpty: Boolean get() = stack.isEmpty()
    var onEmptyListener: () -> Unit = {}


    private var stack: Stack<ViewController> = Stack()

    fun setStack(newStack: Stack<ViewController>, animationSet: AnimationSet? = defaultPushAnimation): Unit {
        val toDispose = stack.filter { !newStack.contains(it) }
        stack = newStack
        swapListener?.invoke(current, animationSet) {
            toDispose.forEach {
                it.dispose()
            }
        }
        onSwap.forEach { it(current) }
    }


    fun push(viewController: ViewController, animationSet: AnimationSet? = defaultPushAnimation) {
        stack.push(viewController)
        swapListener?.invoke(current, animationSet) {}
        onSwap.forEach { it(current) }
    }

    fun pop(animationSet: AnimationSet? = defaultPopAnimation) {
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

    fun root(animationSet: AnimationSet? = defaultPopAnimation) {
        val toDispose = ArrayList<ViewController>(stack)
        toDispose.removeAt(0)
        swapListener?.invoke(current, animationSet) {
            toDispose.forEach {
                it.dispose()
            }
        }
        onSwap.forEach { it(current) }
    }

    fun back(predicate: (ViewController) -> Boolean, animationSet: AnimationSet? = defaultPopAnimation) {
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

    fun swap(viewController: ViewController, animationSet: AnimationSet? = defaultSwapAnimation) {
        val toDispose = stack.pop()
        stack.push(viewController)
        swapListener?.invoke(current, animationSet) {
            toDispose.dispose()
        }
        onSwap.forEach { it(current) }
    }

    fun reset(viewController: ViewController, animationSet: AnimationSet? = defaultSwapAnimation) {
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