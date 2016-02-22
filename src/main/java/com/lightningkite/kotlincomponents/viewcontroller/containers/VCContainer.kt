package com.lightningkite.kotlincomponents.viewcontroller.containers

import android.content.res.Resources
import com.lightningkite.kotlincomponents.Disposable
import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import java.util.*

/**
 * Something that contains [ViewController]s and handles the changes between them.
 * Created by jivie on 10/12/15.
 */
interface VCContainer : Disposable {

    var swapListener: ((newVC: ViewController, AnimationSet?, onFinish: () -> Unit) -> Unit)?
    val onSwap: MutableList<(ViewController) -> Unit>

    val current: ViewController

    fun onBackPressed(backAction: () -> Unit) {
        current.onBackPressed (backAction)
    }

    fun getTitle(resources: Resources): String {
        return current.getTitle(resources)
    }
}

abstract class VCContainerImpl : VCContainer {
    override var swapListener: ((newVC: ViewController, AnimationSet?, onFinish: () -> Unit) -> Unit)? = null
    override val onSwap: MutableList<(ViewController) -> Unit> = ArrayList()
}