package com.lightningkite.kotlincomponents.viewcontroller.containers

import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import java.util.*

/**
 * Created by jivie on 10/12/15.
 */
open class VCStack(): VCContainerImpl(){
    override val current: ViewController get() = stack.peek()

    val size:Int get() = stack.size()
    val isEmpty: Boolean get() = stack.isEmpty()
    var onEmptyListener: ()->Unit = {}


    private val stack: Stack<ViewController> = Stack()



    fun push(viewController: ViewController, animationSet: AnimationSet? = AnimationSet.slidePush) {
        stack.push(viewController)
        swapListener?.invoke(current, animationSet)
    }
    fun pop(animationSet: AnimationSet? = AnimationSet.slidePop){
        stack.pop().dispose()
        if(stack.size() == 0){
            onEmptyListener()
        } else {
            swapListener?.invoke(current, animationSet)
        }
    }
    fun back(predicate: (ViewController)->Boolean, animationSet: AnimationSet? = AnimationSet.slidePop){
        while(!predicate(stack.peek())){
            stack.pop()
            if(stack.size() == 0) throw IllegalArgumentException("There is no view controller that matches this predicate!")
        }
        swapListener?.invoke(current, animationSet)
    }
    fun swap(viewController: ViewController, animationSet: AnimationSet? = null) {
        stack.pop()
        stack.push(viewController)
        swapListener?.invoke(current, animationSet)
    }
    fun reset(viewController: ViewController, animationSet: AnimationSet? = null) {
        stack.clear()
        stack.push(viewController)
        swapListener?.invoke(current, animationSet)
    }

    override fun dispose() {
        for(vc in stack){
            vc.dispose()
        }
    }
}