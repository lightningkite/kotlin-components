package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.lightningkite.kotlincomponents.run
import org.jetbrains.anko._LinearLayout

/**
 * Created by jivie on 7/24/15.
 */
public class SplitViewController(left: ViewController, right: ViewController, ratio: Float) : ViewController, ViewControllerStack {


    public val left: ViewController = left
    public val right: ViewController = right
    public var leftView: View? = null
    public var rightView: View? = null
    public var layout: LinearLayout? = null
    public var ratio: Float = ratio

    public var stack: ViewControllerStack? = null

    override fun make(context: Context, stack: ViewControllerStack): View {
        this.stack = stack
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

    override fun pushView(newController: ViewController, onResult: (result: Any?) -> Unit) {
        stack?.pushView(newController, onResult)
    }

    override fun popView() {
        stack?.popView()
    }

    override fun resetView(newController: ViewController) {
        stack?.resetView(newController)
    }

    override fun replaceView(newController: ViewController) {
        stack?.replaceView(newController)
    }

    override val result: Any?
        get() {
            if (left.result != null) return left.result
            else return right.result
        }

    override fun canPop(): Boolean {
        return left.canPop() && right.canPop()
    }

    override fun dispose(view: View) {
        if (leftView != null) {
            left.dispose(leftView!!)
        }
        if (rightView != null) {
            right.dispose(rightView!!)
        }
    }

    override fun startIntent(intent: Intent, onResult: (result: Int, data: Intent?) -> Unit, options: Bundle) {
        stack?.startIntent(intent, onResult, options)
    }

}