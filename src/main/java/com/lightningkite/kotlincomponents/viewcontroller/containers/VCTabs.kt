package com.lightningkite.kotlincomponents.viewcontroller.containers

import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.ViewController

/**
 * Used to create left/right tabs.
 * @param startIndex The first view controller's index to show.
 * @param vcs The view controllers to display.
 * Created by jivie on 10/14/15.
 */
class VCTabs(startIndex: Int, vararg vcs: ViewController) : VCContainerImpl() {

    val viewControllers: Array<ViewController> = Array(vcs.size, { vcs[it] })
    var index: Int = startIndex

    override val current: ViewController get() = viewControllers[index]

    fun swap(newIndex: Int) {
        if (index == newIndex) return;
        swapListener?.invoke(viewControllers[newIndex],
                if (newIndex > index)
                    AnimationSet.slidePush
                else
                    AnimationSet.slidePop
        ){}
        index = newIndex
    }

    override fun dispose() {
        viewControllers.forEach { it.dispose() }
    }

}