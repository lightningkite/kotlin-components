package com.lightningkite.kotlincomponents.viewcontroller.implementations

import android.view.View
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContainer

/**
 * Created by jivie on 10/14/15.
 */
class ContainerVC(val container:VCContainer): ViewController {

    var vcView:VCView? = null

    override fun make(activity: VCActivity): View {
        vcView = VCView(activity)
        vcView!!.attach(container)
        return vcView!!
    }

    override fun unmake(view: View) {
        vcView?.detatch()
        super.unmake(view)
    }

    override fun dispose() {
        container.dispose()
        super.dispose()
    }

    override fun onBackPressed(backAction:()->Unit) {
        vcView?.container?.onBackPressed(backAction)
    }
}