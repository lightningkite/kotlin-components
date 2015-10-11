package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.lightningkite.kotlincomponents.animation.AnimationSet
import java.util.*

/**
 * Created by jivie on 10/8/15.
 */
open class FrameViewControllerStack(
        override val stack: Stack<ViewControllerData>,
        override val intentSender: IntentSender,
        override var defaultAnimationSetPush: AnimationSet? = AnimationSet.slidePush,
        override var defaultAnimationSetPop: AnimationSet? = AnimationSet.slidePop
) : ViewController, ViewControllerStack {


    private var frame: FrameLayout? = null
    private var currentViewController: ViewController? = null
    private var currentView: View? = null

    override fun make(context: Context, stack: ViewControllerStack): View {
        frame = FrameLayout(context)
        if (this.stack.last() != null) {
            swap(this.stack.last(), AnimationSet.fade)
        }
        return frame!!;
    }

    override fun unmake(view: View) {
        if (currentView != null) {
            stack.lastOrNull()?.unmake(currentView!!)
        }
        this.frame = null
        super.unmake(view)
    }

    override fun dispose() {
        for (vc in stack) {
            vc.dispose()
        }
        super.dispose()
    }


    override fun popView(result: Any?, animationSet: AnimationSet?): Boolean {
        if (!super.popView(result, animationSet)) {
            return false
        }
        return true
    }

    override fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit, options: Bundle) {
        frame?.context?.startActivity(intent, options)
    }

    override fun swap(
            newViewController: ViewController,
            animationSet: AnimationSet?
    ) {
        val frame = frame ?: return
        val newView = newViewController.make(frame.context, this)
        if (currentView == null) {
            frame.addView(newView)
        } else {
            val oldView = currentView!!
            val oldViewController = currentViewController!!

            val animateOut = animationSet?.animateOut
            if (animateOut != null) {
                oldView.animateOut(frame).withEndAction {
                    oldViewController.unmake(oldView)
                    frame.removeView(oldView)
                }.start()
            } else {
                oldViewController.unmake(oldView)
                frame.removeView(oldView)
            }

            val animateIn = animationSet?.animateIn
            frame.addView(newView)
            if (animateIn != null) {
                newView.animateIn(frame)
            }
        }
        currentView = newView
        currentViewController = newViewController
    }
}