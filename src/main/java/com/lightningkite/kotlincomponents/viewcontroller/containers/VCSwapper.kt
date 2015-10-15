package com.lightningkite.kotlincomponents.viewcontroller.containers

import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import java.util.*

/**
 * Created by jivie on 10/14/15.
 */
class VCSwapper(startVC:ViewController): VCContainerImpl(){

    override var current: ViewController = startVC

    fun swap(vc:ViewController, animation:AnimationSet? = null){
        swapListener?.invoke(vc, animation)
    }

    override fun dispose() {
        current.dispose()
    }

}