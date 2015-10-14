package com.lightningkite.kotlincomponents.viewcontroller.implementations

import android.app.AlertDialog
import android.content.Context
import android.view.Window
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContainer
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCStack

/**
 * Created by jivie on 9/25/15.
 */
public fun VCActivity.dialog(
        vcMaker: (AlertDialog)->ViewController
) {
    var dialog: AlertDialog? = null
    var dismissed: Boolean = false
    val builder = AlertDialog.Builder(this)
    dialog = builder.create()
    dialog!!.setView(vcMaker(dialog).make(this), 0, 0, 0, 0)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.show()
}

public fun VCActivity.dialog(
        container: VCContainer
) {
    var dialog: AlertDialog? = null
    var dismissed: Boolean = false
    val builder = AlertDialog.Builder(this)
    val view = VCView(this)
    view.attach(container)
    dialog = builder.create()
    dialog!!.setView(view, 0, 0, 0, 0)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setOnDismissListener {
        view.detatch()
    }
    dialog.show()
}

public fun VCActivity.dialog(
        stack: VCStack
) {
    var dialog: AlertDialog? = null
    var dismissed: Boolean = false
    val builder = AlertDialog.Builder(this)

    val view = VCView(this)
    stack.onEmptyListener = {
        dialog?.dismiss()
    }
    view.attach(stack)

    dialog = builder.create()
    dialog!!.setView(view, 0, 0, 0, 0)
    dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog!!.setOnDismissListener {
        view.detatch()
    }
    dialog!!.show()
}