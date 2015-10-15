package com.lightningkite.kotlincomponents.viewcontroller.implementations

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.logging.logD
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContainer
import org.jetbrains.anko.frameLayout
import java.util.*

/**
 * Created by jivie on 10/12/15.
 */
abstract class VCActivity : Activity() {

    companion object {
        val returns: HashMap<Int, (Int, Intent?) -> Unit> = HashMap()
    }

    fun startIntent(intent: Intent, options: Bundle = Bundle.EMPTY, onResult: (Int, Intent?) -> Unit) {
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

    var disposeOnComplete:Boolean = false
    var container:VCContainer? = null
    fun attach(newContainer: VCContainer, newDisposeOnComplete:Boolean = false){
        if(disposeOnComplete) container?.dispose()
        disposeOnComplete = newDisposeOnComplete
        container = newContainer
        newContainer.swapListener = swap
        swap(newContainer.current, null)
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
                container?.swapComplete()
            } else {
                val animateOut = animation.animateOut
                oldView.animateOut(frame).withEndAction {
                    old.unmake(oldView)
                    container?.swapComplete()
                }.start()
                val animateIn = animation.animateIn
                currentView!!.animateIn(frame).start()
            }
        } else{
            container?.swapComplete()
        }
    }

    lateinit var frame:FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        frame = FrameLayout(this)
        setContentView(frame)
    }

    override fun onDestroy() {
        if(disposeOnComplete) container?.dispose()
        current?.unmake(currentView!!)
        container?.swapListener = null
        super.onDestroy()
    }

}