package com.lightningkite.kotlincomponents.viewcontroller.implementations

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Window
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCStack

/**
 * Allows you to create a dialog using a view controller.
 * Created by jivie on 9/25/15.
 */
@Deprecated("This uses a standard alert dialog which is deprecated.")
fun VCActivity.dialog(
        vcMaker: (AlertDialog)->ViewController
) {
    var dialog: AlertDialog? = null
    var dismissed: Boolean = false
    val builder = AlertDialog.Builder(this)
    dialog = builder.create()
    val viewController = vcMaker(dialog)
    val view = viewController.make(this)
    dialog!!.setView(view, 0, 0, 0, 0)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setOnDismissListener(DialogInterface.OnDismissListener { viewController.unmake(view) })
    dialog.show()
}

@Deprecated("This uses a standard alert dialog which is deprecated.")
fun VCActivity.dialog(
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
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setOnDismissListener {
        view.detatch()
    }
    dialog.show()
}