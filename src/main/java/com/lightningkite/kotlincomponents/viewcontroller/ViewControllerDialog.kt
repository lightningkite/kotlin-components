package com.lightningkite.kotlincomponents.viewcontroller

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import com.lightningkite.kotlincomponents.animation.AnimationSet

/**
 * Created by jivie on 9/25/15.
 */
public fun Context.dialogSingleScreen(
        vc: ViewController,
        onResult: (result: Any?) -> Unit = {}
) {
    var dialog: AlertDialog? = null
    var dismissed: Boolean = false
    val builder = AlertDialog.Builder(this)
    val fauxStack: ViewControllerStack = object : ViewControllerStack {
        override fun pushView(newController: ViewController, onResult: (Any?) -> Unit) {
            throw UnsupportedOperationException()
        }

        override fun popView(): Boolean {
            dialog?.dismiss()
            dismissed = true
            onResult(vc.result)
            return false
        }

        override fun resetView(newController: ViewController) {
            throw UnsupportedOperationException()
        }

        override fun replaceView(newController: ViewController) {
            throw UnsupportedOperationException()
        }

        override fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit, options: Bundle) {
            throw UnsupportedOperationException()
        }

    }
    if (dismissed) return //catches early dismisses
    dialog = builder.create()
    dialog!!.setView(vc.make(this@dialogSingleScreen, fauxStack), 0, 0, 0, 0)
    dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog!!.show()
}

public fun Activity.dialogMultiScreen(
        vc: ViewController,
        listener: ViewControllerView.IntentListener,
        tag: String = "dialog",
        animationSetPush: AnimationSet = AnimationSet.slidePush,
        animationSetPop: AnimationSet = AnimationSet.slidePop,
        onResult: (result: Any?) -> Unit = {}
) {

    var dialog: AlertDialog? = null
    var dismissed: Boolean = false
    val builder = AlertDialog.Builder(this, android.R.style.Theme_Translucent_NoTitleBar)
    val vcv: ViewControllerView = ViewControllerView(this@dialogMultiScreen, vc, listener, tag, animationSetPush, animationSetPop)

    val stackInterface: ViewControllerStack = object : ViewControllerStack {
        override fun pushView(newController: ViewController, onResult: (Any?) -> Unit) = vcv.pushView(newController, onResult)
        override fun popView(): Boolean {
            val result = vcv.stack.firstOrNull()?.result
            if (!vcv.popView()) {
                dismissed = true
                dialog?.dismiss()
                onResult(result)
            }
            return true
        }

        override fun resetView(newController: ViewController) = vcv.resetView(newController)
        override fun replaceView(newController: ViewController) = vcv.replaceView(newController)
        override fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit, options: Bundle) = vcv.startIntent(intent, onResult, options)
    }

    if (dismissed) return //catches early dismisses
    dialog = builder.create()
    dialog!!.setView(vc.make(this@dialogMultiScreen, stackInterface), 0, 0, 0, 0)
    dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog!!.show()
}