package com.lightningkite.kotlincomponents.viewcontroller

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import com.lightningkite.kotlincomponents.animation.AnimationSet
import java.util.*

/**
 * Created by jivie on 9/25/15.
 */
public fun Context.dialogSingleScreen(
        vc: ViewController,
        stack: ViewControllerStack? = null,
        onResult: (result: Any?) -> Unit = {}
) {
    var dialog: AlertDialog? = null
    var dismissed: Boolean = false
    val builder = AlertDialog.Builder(this)
    val fauxStack: ViewControllerStack = object : ViewControllerStack.DummyStack() {
        override val intentSender: IntentSender = stack?.intentSender ?: object : IntentSender {
            override fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit, options: Bundle) {
                throw UnsupportedOperationException()
            }
        }

        override fun popView(result: Any?, animationSet: AnimationSet?): Boolean {
            dialog?.dismiss()
            dismissed = true
            onResult(result)
            vc.dispose()
            return false
        }
    }
    if (dismissed) return //catches early dismisses
    dialog = builder.create()
    dialog!!.setView(vc.make(this@dialogSingleScreen, fauxStack), 0, 0, 0, 0)
    dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog!!.show()
}

//UNTESTED!
public fun Activity.dialogMultiScreen(
        stack: Stack<ViewControllerData>,
        upperStack: ViewControllerStack? = null,
        animationSetPush: AnimationSet = AnimationSet.slidePush,
        animationSetPop: AnimationSet = AnimationSet.slidePop,
        onResult: (result: Any?) -> Unit = {}
) {

    var dialog: AlertDialog? = null
    var dismissed: Boolean = false
    val builder = AlertDialog.Builder(this, android.R.style.Theme_Translucent_NoTitleBar)
    var fvcs: FrameViewControllerStack
    val intentSender = upperStack?.intentSender ?: object : IntentSender {
        override fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit, options: Bundle) {
            throw UnsupportedOperationException()
        }
    }

    fvcs = object : FrameViewControllerStack(stack, intentSender, animationSetPush, animationSetPop) {
        override fun popView(result: Any?, animationSet: AnimationSet?): Boolean {
            if (!super.popView(animationSet)) {
                dialog?.dismiss()
                dismissed = true
                onResult(result)
                dispose()
                return false
            }
            return true
        }
    }

    if (dismissed) return //catches early dismisses
    dialog = builder.create()
    dialog!!.setView(fvcs.make(this@dialogMultiScreen, ViewControllerStack.dummy), 0, 0, 0, 0)
    dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog!!.show()
}