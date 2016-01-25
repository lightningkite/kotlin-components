package com.lightningkite.kotlincomponents.viewcontroller

import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlincomponents.Disposable
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContainer
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCView
import java.util.*

/**
 * This class is a wrapper around ViewController that cleans up after itself - you can use
 * autoDispose(x) to dispose x when the view controller is disposed, and use x to call
 * make(View) and unmake(View) when those view controller's functions are invoked.
 * Also has a convenience function for placing a view controller within this view controller.
 * Created by jivie on 9/30/15.
 */
public abstract class AutocleanViewController : ViewController {

    private val autoDispose: ArrayList<Disposable> = ArrayList()
    private val listeners: ArrayList<Listener> = ArrayList()
    private val innerViews: MutableMap<ViewController, View> = HashMap()

    /**
     * Disposes the object given when this view controller is disposed.
     */
    public fun <T : Disposable> autoDispose(vc: T): T {
        autoDispose.add(vc)
        return vc
    }

    /**
     * Calls make(View) and unmake(View) when this view controller has those functions called on it.
     */
    public fun <T : Listener> listener(l: T): T {
        listeners.add(l)
        autoDispose.add(l)
        return l
    }

    /**
     * Embeds a view controller within this view controller.  Nesting!
     * The view controller passed in should NOT be created within here and should be autodisposed by
     * this view controller.
     */
    public fun <T : ViewController> ViewGroup.viewController(viewController: T, initCode: T.() -> Unit = {}): View {
        assert(context is VCActivity)
        viewController.initCode()
        val view = viewController.make(context as VCActivity)
        innerViews.put(viewController, view)
        addView(view)
        return view
    }

    fun ViewGroup.viewController(container:VCContainer): VCView{
        val vcview = VCView(context as VCActivity)
        vcview.attach(container)
        listeners.add(object: Listener{
            override fun make(activity: VCActivity) {}
            override fun dispose() {}
            override fun unmake(view: View) {
                vcview.detatch()
            }
        })
        addView(vcview)
        return vcview
    }

    /**
     * Make sure this is called in your subclasses, as it calls all of the listeners.
     */
    override fun make(activity: VCActivity): View {
        for (listener in listeners) {
            listener.make(activity)
        }
        return View(activity)
    }

    /**
     * Make sure this is called in your subclasses, as it calls all of the listeners.
     */
    override fun unmake(view: View) {
        for (listener in listeners) {
            listener.unmake(view)
        }
        for ((controller, innerView) in innerViews) {
            controller.unmake(innerView)
        }
        innerViews.clear()
        super.unmake(view)
    }

    /**
     * Make sure this is called in your subclasses, as it handles the auto-disposing.
     */
    override fun dispose() {
        for (disposable in autoDispose) {
            disposable.dispose()
        }
        listeners.clear()
        autoDispose.clear()
        super.dispose()
    }

    public interface Listener : Disposable {
        public fun make(activity: VCActivity)
        public fun unmake(view: View)
    }
}