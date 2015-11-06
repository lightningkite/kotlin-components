package com.lightningkite.kotlincomponents.viewcontroller.containers

import com.lightningkite.kotlincomponents.Disposable
import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import java.util.*

/**
 * Something that contains [ViewController]s and handles the changes between them.
 * Created by jivie on 10/12/15.
 */
interface VCContainer: Disposable{
    var onSwapCompleteListeners: ArrayList<() -> Unit>
    fun swapComplete(){
        onSwapCompleteListeners.forEach{it()}
        onSwapCompleteListeners.clear()
    }
    var swapListener:((newVC: ViewController, AnimationSet?, onFinish: ()->Unit)->Unit)?

    val current: ViewController

    fun onBackPressed(backAction:()->Unit){
        backAction()
    }
}
abstract class VCContainerImpl: VCContainer {
    override var onSwapCompleteListeners: ArrayList<() -> Unit> = ArrayList()
    override var swapListener:((newVC: ViewController, AnimationSet?, onFinish: ()->Unit)->Unit)? = null
}