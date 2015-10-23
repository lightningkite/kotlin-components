package com.lightningkite.kotlincomponents.viewcontroller.implementations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.ListView
import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContainer
import org.jetbrains.anko.onClick

/**
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

    var current: ViewController? = null
    var currentView: View? = null
    val swap = fun(vc: ViewController, preferredAnimation: AnimationSet?, onFinish:()->Unit){
        val oldView = currentView
        val old = current
        val animation = preferredAnimation ?: defaultAnimation
        current = vc
        currentView = vc.make(activity)
        if(currentView !is AbsListView){
            currentView!!.onClick {  }
        }
        this.addView(currentView)
        if(old != null && oldView != null){
            if(animation == null){
                old.unmake(oldView)
                onFinish()
            } else {
                val animateOut = animation.animateOut
                oldView.animateOut(this).withEndAction {
                    old.unmake(oldView)
                    onFinish()
                }.start()
                val animateIn = animation.animateIn
                currentView!!.animateIn(this).start()
            }
        }
    }



}