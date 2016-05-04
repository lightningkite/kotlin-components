package com.lightningkite.kotlincomponents.viewcontroller.implementations

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContainer
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCStack
import java.util.*

/**
 * All activities hosting [ViewController]s must be extended from this one.
 * It handles the calling of other activities with [onActivityResult], the attaching of a
 * [VCContainer], and use the back button on the [VCContainer].
 * Created by jivie on 10/12/15.
 */
class VCDialogActivity : VCActivity() {

    class ContainerData(val container: VCContainer, val layoutParamsSetup: WindowManager.LayoutParams.() -> Unit)

    companion object {
        const val EXTRA_CONTAINER: String = "VCDialogActivity.containerId"
        val containers: HashMap<Int, ContainerData> = HashMap()
    }

    var myIndex = 0
    var myContainerData: ContainerData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myIndex = intent.getIntExtra(EXTRA_CONTAINER, 0)
        myContainerData = containers[myIndex] ?: return
        attach(myContainerData!!.container)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (myContainerData != null) {
            windowManager.updateViewLayout(
                    window.decorView,
                    (window.decorView.layoutParams as WindowManager.LayoutParams)
                            .apply(myContainerData!!.layoutParamsSetup))
        }
    }

    override fun finish() {
        containers.remove(myIndex)
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!containers.containsKey(myIndex)) {
            myContainerData?.container?.dispose()
        }
    }
}

inline fun Activity.viewControllerDialog(vcMaker: (VCStack) -> ViewController, noinline layoutParamModifier: WindowManager.LayoutParams.() -> Unit = {}) {
    viewControllerDialog(VCStack().apply { push(vcMaker(this)) }, layoutParamModifier)
}

inline fun Activity.viewControllerDialog(container: VCContainer, noinline layoutParamModifier: WindowManager.LayoutParams.() -> Unit = {}) {
    val id: Int = container.hashCode()
    VCDialogActivity.containers[id] = VCDialogActivity.ContainerData(container, layoutParamModifier)
    startActivity(Intent(this, VCDialogActivity::class.java).apply {
        putExtra(VCDialogActivity.EXTRA_CONTAINER, id)
    })
}

inline fun VCActivity.viewControllerDialog(container: VCContainer, noinline layoutParamModifier: WindowManager.LayoutParams.() -> Unit = {}, crossinline onDismissed: () -> Unit) {
    val id: Int = container.hashCode()
    VCDialogActivity.containers[id] = VCDialogActivity.ContainerData(container, layoutParamModifier)
    startIntent(
            Intent(this, VCDialogActivity::class.java).apply {
                putExtra(VCDialogActivity.EXTRA_CONTAINER, id)
            },
            onResult = { code, data -> onDismissed() }
    )
}