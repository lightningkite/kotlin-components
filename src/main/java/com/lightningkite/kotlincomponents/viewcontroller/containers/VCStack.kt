package com.lightningkite.kotlincomponents.viewcontroller.containers

import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import java.util.*

/**
 * Created by jivie on 10/12/15.
 */
class VCStack(vararg controllers:ViewController): VCContainer{

    private val stack: Stack<ViewController> = Stack()
    init{
        for(controller in controllers){
            stack.push(controller)
        }
    }

    override var swapListener: (ViewController, AnimationSet?) -> Unit = {controller, animation -> }
    override val current: ViewController get() = stack.peek()

    fun push(viewController: ViewController, animationSet: AnimationSet? = null) {
        stack.push(viewController)
        swapListener(current, animationSet)
    }
    fun pop(animationSet: AnimationSet? = null){
        stack.pop()
        swapListener(current, animationSet)
    }
    fun back(predicate: (ViewController)->Boolean, animationSet: AnimationSet? = null){
        while(!predicate(stack.peek())){
            stack.pop()
            if(stack.size() == 0) throw IllegalArgumentException("There is no view controller that matches this predicate!")
        }
        swapListener(current, animationSet)
    }
    fun swap(viewController: ViewController, animationSet: AnimationSet? = null) {
        stack.pop()
        stack.push(viewController)
        swapListener(current, animationSet)
    }
    fun reset(viewController: ViewController, animationSet: AnimationSet? = null) {
        stack.clear()
        stack.push(viewController)
        swapListener(current, animationSet)
    }

}