package com.lightningkite.kotlincomponents.viewcontroller.containers

import com.lightningkite.kotlincomponents.Disposable
import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import java.util.*

/**
 * Created by jivie on 10/12/15.
 */
interface VCContainer: Disposable{
    var onSwapCompleteListeners: ArrayList<() -> Unit>
    fun swapComplete(){
        onSwapCompleteListeners.forEach{it()}
        onSwapCompleteListeners.clear()
    }
    var swapListener:((newVC: ViewController, AnimationSet?)->Unit)?

    val current: ViewController
}
abstract class VCContainerImpl: VCContainer {
    override var onSwapCompleteListeners: ArrayList<() -> Unit> = ArrayList()
    override var swapListener:((newVC: ViewController, AnimationSet?)->Unit)? = null
}