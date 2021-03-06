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

    var wholeViewAnimatingIn: Boolean = false
    var killViewAnimateOutCalled: Boolean = false

    var container:VCContainer? = null
    fun attach(newContainer: VCContainer){
        container = newContainer
        newContainer.swapListener = swap
        swap(newContainer.current, null){}
    }
    fun detatch(){
        unmake()
        container?.swapListener = null
    }
    fun unmake(){
        if (!killViewAnimateOutCalled) {
            current?.animateOutStart(activity, currentView!!)
            killViewAnimateOutCalled = true
        }
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
                old.animateOutStart(activity, oldView)
                old.unmake(oldView)
                removeView(oldView)
                onFinish()
                current?.animateInComplete(activity, currentView!!)
            } else {
                val animateOut = animation.animateOut
                old.animateOutStart(activity, oldView)
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
            if (!wholeViewAnimatingIn) {
                current?.animateInComplete(activity, currentView!!)
            }
        }
        killViewAnimateOutCalled = false
    }

    fun animateInComplete(activity: VCActivity, view: View) {
        current?.animateInComplete(activity, currentView!!)
    }

    fun animateOutStart(activity: VCActivity, view: View) {
        killViewAnimateOutCalled = true
        current?.animateOutStart(activity, currentView!!)
    }
}