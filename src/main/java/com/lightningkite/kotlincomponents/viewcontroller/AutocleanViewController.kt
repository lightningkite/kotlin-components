package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlincomponents.Disposable
import java.util.*

/**
 * Created by jivie on 9/30/15.
 */
public abstract class AutocleanViewController : ViewController {

    private val autoDispose: ArrayList<Disposable> = ArrayList()
    private val listeners: ArrayList<Listener> = ArrayList()
    private val innerViews: MutableMap<ViewController, View> = HashMap()

    public fun <T : Disposable> autoDispose(vc: T): T {
        autoDispose.add(vc)
        return vc
    }

    public fun <T : Listener> listener(l: T): T {
        listeners.add(l)
        autoDispose.add(l)
        return l
    }

    public fun <T : ViewController> ViewGroup.viewController(viewController: T, stack: ViewControllerStack = ViewControllerStack.dummy, initCode: T.() -> Unit): View {
        val view = viewController.make(context, stack)
        innerViews.put(viewController, view)
        addView(view)
        return view
    }

    override fun make(context: Context, stack: ViewControllerStack): View {
        for (listener in listeners) {
            listener.make(context, stack)
        }
        return View(context)
    }

    override fun unmake(view: View) {
        for (listener in listeners) {
            listener.unmake(view)
        }
        for ((controller, innerView) in innerViews) {
            controller.unmake(innerView)
        }
        super.unmake(view)
    }

    override fun dispose() {
        for (disposable in autoDispose) {
            disposable.dispose()
        }
        super.dispose()
    }

    public interface Listener : Disposable {
        public fun make(context: Context, stack: ViewControllerStack)
        public fun unmake(view: View)
    }
}