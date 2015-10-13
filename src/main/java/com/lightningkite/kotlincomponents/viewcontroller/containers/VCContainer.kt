package com.lightningkite.kotlincomponents.viewcontroller.containers

import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.ViewController

/**
 * Created by jivie on 10/12/15.
 */
interface VCContainer {
    var swapListener:(newVC: ViewController, AnimationSet?)->Unit
    val current: ViewController
}