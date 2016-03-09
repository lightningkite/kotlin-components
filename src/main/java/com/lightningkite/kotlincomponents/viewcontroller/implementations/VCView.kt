package com.lightningkite.kotlincomponents.viewcontroller.implementations

import android.view.Gravity
import android.view.View
import android.widget.AbsListView
import android.widget.FrameLayout
import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContainer
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.onClick

/**
 * A [View] that has a [VCContainer].
 * Created by jivie on 10/13/15.
 */
open class VCView(val activity: VCActivity): FrameLayout(activity){

    open val defaultAnimation: AnimationSet? = AnimationSet.fade

    var container:VCContainer? = null
    fun attach(newContainer: VCContainer){
        container = newContainer
        newContainer.swapListener = swap
        swap(newContainer.current, null){}
    }
    fun detatch(){
        container?.swapListener = null
    }
    fun unmake(){
        current?.unmake(currentView!!)
        if(currentView != null){
            removeView(currentView)
        }
        current = null
        currentView = null
    }

    var current: ViewController? = null
    var currentView: View? = null
    val swap = fun(vc: ViewController, preferredAnimation: AnimationSet?, onFinish:()->Unit){
        val oldView = currentView
        val old = current
        val animation = preferredAnimation ?: defaultAnimation
        current = vc
        currentView = vc.make(activity).apply {
            layoutParams = FrameLayout.LayoutParams(matchParent, matchParent, Gravity.TOP or Gravity.CENTER_HORIZONTAL)
            if (this !is AbsListView) {
                onClick { }
            }
        }
        this.addView(currentView)
        if(old != null && oldView != null){
            if(animation == null){
                old.unmake(oldView)
                removeView(oldView)
                onFinish()
                current?.animateInComplete(activity, currentView!!)
            } else {
                val animateOut = animation.animateOut
                oldView.animateOut(this).withEndAction {
                    old.unmake(oldView)
                    removeView(oldView)
                    onFinish()
                }.start()
                val animateIn = animation.animateIn
                currentView!!.animateIn(this).withEndAction {
                    current?.animateInComplete(activity, currentView!!)
                }.start()
            }
        } else {
            current?.animateInComplete(activity, currentView!!)
        }
    }



}