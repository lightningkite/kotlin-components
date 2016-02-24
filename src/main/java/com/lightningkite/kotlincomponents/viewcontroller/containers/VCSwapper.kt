package com.lightningkite.kotlincomponents.viewcontroller.containers

import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.ViewController

/**
 * The simplest [VCContainer] implementation.  Can swap the [ViewController] that is visible for
 * another one.  Keeps no history of past [ViewController]s.
 * Created by jivie on 10/14/15.
 */
class VCSwapper(startVC:ViewController): VCContainerImpl(){

    override var current: ViewController = startVC

    fun swap(vc:ViewController, animation:AnimationSet? = null){
        val toDispose = current
        current = vc
        swapListener?.invoke(vc, animation){
            toDispose.dispose()
        }
        onSwap.forEach { it(current) }
    }

    override fun dispose() {
        current.dispose()
    }

}