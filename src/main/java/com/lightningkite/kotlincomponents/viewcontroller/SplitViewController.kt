package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.run
import org.jetbrains.anko._LinearLayout
import java.util.*

/**
 * Shows two view controllers side by side.
 * Navigation just accesses the parent stack.
 * Untested.
 * Created by jivie on 7/24/15.
 */
public class SplitViewController(left: ViewController, right: ViewController, ratio: Float) : ViewController, ViewControllerStack {
    override fun swap(newViewController: ViewController, animationSet: AnimationSet?) {
    }

    override val intentSender: IntentSender get() = myStack?.intentSender ?: object : IntentSender {
        override fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit, options: Bundle) {
            throw UnsupportedOperationException()
        }
    }
    override val stack: Stack<ViewControllerData> = ViewControllerStack.dummy.stack


    public val left: ViewController = left
    public val right: ViewController = right
    public var leftView: View? = null
    public var rightView: View? = null
    public var layout: LinearLayout? = null
    public var ratio: Float = ratio

    public var myStack: ViewControllerStack? = null

    override fun make(context: Context, stack: ViewControllerStack): View {
        this.myStack = stack
        layout = _LinearLayout(context).run {
            orientation = LinearLayout.HORIZONTAL
            val leftView = left.make(context, this@SplitViewController)
            val rightView = right.make(context, this@SplitViewController)
            if (ratio == 0f) {
                leftView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                rightView.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            } else if (ratio == 1f) {
                leftView.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
                rightView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
            } else {
                leftView.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, ratio)
                rightView.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f - ratio)
            }
            addView(leftView)
            addView(rightView)
        }
        return layout!!
    }

    override fun pushView(newController: ViewController, animationSet: AnimationSet?, onResult: (Any?) -> Unit) {
        myStack?.pushView(newController, animationSet, onResult)
    }

    override fun popView(animationSet: AnimationSet?): Boolean {
        return myStack?.popView(animationSet) ?: true
    }

    override fun resetView(newController: ViewController, animationSet: AnimationSet?) {
        myStack?.resetView(newController, animationSet)
    }

    override fun replaceView(newController: ViewController, animationSet: AnimationSet?) {
        myStack?.replaceView(newController, animationSet)
    }

    override fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit, options: Bundle) {
        myStack?.startIntent(intent, onResult, options)
    }

    override val result: Any?
        get() {
            if (left.result != null) return left.result
            else return right.result
        }

    override fun canPop(): Boolean {
        return left.canPop() && right.canPop()
    }

    override fun unmake(view: View) {
        if (leftView != null) {
            left.unmake(leftView!!)
        }
        if (rightView != null) {
            right.unmake(rightView!!)
        }
    }

    override fun dispose() {
        left.dispose()
        right.dispose()
    }

}