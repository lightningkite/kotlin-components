package com.lightningkite.kotlincomponents.viewcontroller.containers

import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.ViewController

/**
 * Created by jivie on 10/14/15.
 */

class VCTabs(startIndex:Int, vararg vcs: ViewController): VCContainerImpl(){

    val viewControllers:Array<ViewController> = Array(vcs.size(), {vcs[it]})
    var index:Int = startIndex

    override var current: ViewController = viewControllers[index]

    fun swap(newIndex:Int){
        if(index == newIndex) return;
        swapListener?.invoke(viewControllers[newIndex],
                if(newIndex > index)
                    AnimationSet.slidePush
                else
                    AnimationSet.slidePop
        )
        index = newIndex
    }

    override fun dispose() {
        current.dispose()
    }

}