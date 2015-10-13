package com.lightningkite.kotlincomponents.viewcontroller.implementations

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContainer
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContext
import org.jetbrains.anko.frameLayout
import java.util.*

/**
 * Created by jivie on 10/12/15.
 */
abstract class VCContainerActivity: Activity(), VCContext {

    companion object {
        val returns: HashMap<Int, (Int, Intent?) -> Unit> = HashMap()
    }

    override fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit, options: Bundle) {
        val generated: Int = (Math.random() * Int.MAX_VALUE).toInt()
        returns[generated] = onResult
        startActivityForResult(intent, generated, options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            returns[requestCode]?.invoke(resultCode, data)
            returns.remove(requestCode)
        }
    }

    open val defaultAnimation: AnimationSet? = AnimationSet.fade

    var container:VCContainer? = null
    fun attach(newContainer: VCContainer){
        container = newContainer
        newContainer.swapListener = swap
    }

    var current:ViewController? = null
    var currentView: View? = null
    val swap = fun(vc: ViewController, preferredAnimation: AnimationSet?){
        val oldView = currentView
        val old = current
        val animation = preferredAnimation ?: defaultAnimation
        current = vc
        currentView = vc.make(this)
        frame.addView(currentView)
        if(old != null && oldView != null){
            if(animation == null){
                old.unmake(oldView)
            } else {
                val animateOut = animation.animateOut
                oldView.animateOut(frame).withEndAction {
                    old.unmake(oldView)
                }.start()
                val animateIn = animation.animateIn
                currentView!!.animateIn(frame).start()
            }
        }
    }

    lateinit var frame:FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        frame = frameLayout()
    }

    override fun onDestroy() {
        current?.unmake(currentView!!)
        container?.swapListener = {a, b ->}
        super.onDestroy()
    }

    override val context: Context
        get() = this

}