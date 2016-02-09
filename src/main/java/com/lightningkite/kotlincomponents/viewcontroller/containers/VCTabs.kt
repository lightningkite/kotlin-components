package com.lightningkite.kotlincomponents.viewcontroller.containers

import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.observable.KObservable
import com.lightningkite.kotlincomponents.viewcontroller.ViewController

/**
 * Used to create left/right tabs.
 * @param startIndex The first view controller's index to show.
 * @param vcs The view controllers to display.
 * Created by jivie on 10/14/15.
 */
class VCTabs(startIndex: Int, vararg vcs: ViewController) : VCContainerImpl() {

    val viewControllers: Array<ViewController> = Array(vcs.size, { vcs[it] })
    val indexObs: KObservable<Int> = KObservable(startIndex)
    var index by indexObs

    override val current: ViewController get() = viewControllers[index]

    fun swap(newIndex: Int) {
        if (index == newIndex) return;
        index = newIndex
    }

    var oldIndex: Int = startIndex
    val onChangeListener: (Int) -> Unit = { it: Int ->
        swapListener?.invoke(viewControllers[it],
                if (it > oldIndex) {
                    AnimationSet.slidePush
                } else {
                    AnimationSet.slidePop
                },
                {})
        oldIndex = it
    }

    init {
        indexObs.add(onChangeListener)
    }

    override fun dispose() {
        indexObs.remove(onChangeListener)
        viewControllers.forEach { it.dispose() }
    }

}