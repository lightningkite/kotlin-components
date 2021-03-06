package com.lightningkite.kotlincomponents.viewcontroller

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlincomponents.Disposable
import com.lightningkite.kotlincomponents.runAll
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContainer
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCView
import java.util.*

/**
 *
 * Created by jivie on 1/19/16.
 *
 */

abstract class StandardViewController() : ViewController {

    val onMake: ArrayList<(View) -> Unit> = ArrayList()
    val onAnimateInComplete: ArrayList<(VCActivity, View) -> Unit> = ArrayList()
    val onAnimateOutStart: ArrayList<(VCActivity, View) -> Unit> = ArrayList()
    val onUnmake: ArrayList<(View) -> Unit> = ArrayList()
    val onDispose: ArrayList<() -> Unit> = ArrayList()

    /**
     * Adds the item to the collection immediately, but removes it when [unmake] is called.
     * The primary use of this is binding things in [make] that need to be removed when [unmake] is called.
     */
    @Deprecated("Use [StandardViewController.listen] instead.")
    fun <T> connectVC(collection: MutableCollection<T>, item: T): T {
        collection.add(item)
        onUnmake.add {
            collection.remove(item)
        }
        return item
    }

    /**
     * Adds the item to the collections immediately, but removes the item from all of the collections when [unmake] is called.
     * The primary use of this is binding things in [make] that need to be removed when [unmake] is called.
     */
    @Deprecated("Use [StandardViewController.listen] instead.")
    fun <T> connectManyVC(vararg collections: MutableCollection<T>, item: T): T {
        for (collection in collections) {
            collection.add(item)
        }
        onUnmake.add {
            for (collection in collections) {
                collection.remove(item)
            }
        }
        return item
    }

    abstract fun makeView(activity: VCActivity): View
    final override fun make(activity: VCActivity): View {
        val view = makeView(activity)
        onMake.runAll(view)
        onMake.clear()
        return view
    }

    override fun unmake(view: View) {
        onUnmake.runAll(view)
        onUnmake.clear()
        super.unmake(view)
    }

    override fun dispose() {
        onDispose.runAll()
        onDispose.clear()
        super.dispose()
    }

    override fun animateInComplete(activity: VCActivity, view: View) {
        onAnimateInComplete.runAll(activity, view)
        onAnimateInComplete.clear()
        super.animateInComplete(activity, view)
    }

    override fun animateOutStart(activity: VCActivity, view: View) {
        onAnimateOutStart.runAll(activity, view)
        onAnimateOutStart.clear()
        super.animateOutStart(activity, view)
    }

    fun <T : Disposable> autoDispose(vc: T): T {
        onDispose.add { vc.dispose() }
        return vc
    }

    inline fun ViewGroup.viewContainer(container: VCContainer): VCView {
        val vcview = VCView(context as VCActivity)
        vcview.wholeViewAnimatingIn = true
        vcview.attach(container)
        onAnimateInComplete.add { activity, view ->
            vcview.animateInComplete(activity, view)
        }
        onAnimateOutStart.add { activity, view ->
            vcview.animateOutStart(activity, view)
        }
        onUnmake.add {
            vcview.detatch()
        }
        addView(vcview)
        return vcview
    }

    inline fun ViewGroup.viewContainer(container: VCContainer, init: VCView.() -> Unit): VCView {
        val vcview = VCView(context as VCActivity)
        vcview.wholeViewAnimatingIn = true
        vcview.attach(container)
        onAnimateInComplete.add { activity, view ->
            vcview.animateInComplete(activity, view)
        }
        onAnimateOutStart.add { activity, view ->
            vcview.animateOutStart(activity, view)
        }
        onUnmake.add {
            vcview.detatch()
        }
        addView(vcview)
        vcview.init()
        return vcview
    }

    inline fun ViewGroup.viewController(controller: ViewController, init: View.() -> Unit): View {
        val view = controller.make(context as VCActivity)
        addView(view)
        view.init()
        return view
    }

    @Deprecated("Please use text resources.  It's better anyways.")
    inline fun Menu.item(textRes: String, iconRes: Int, crossinline setup: MenuItem.() -> Unit) {
        var menuItem: MenuItem? = null
        onMake.add {
            println("adding menu item $textRes")
            menuItem = add(textRes).apply {
                setIcon(iconRes)
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }.apply(setup)
        }
        onAnimateOutStart.add { a, v ->
            println("removing menu item $textRes")
            removeItem((menuItem ?: return@add).itemId)
        }
    }

    inline fun Menu.item(textRes: Int, iconRes: Int, groupId: Int = 0, id: Int = textRes + iconRes, order: Int = Menu.CATEGORY_CONTAINER or (textRes and 0xFFFF), crossinline setup: MenuItem.() -> Unit) {
        var menuItem: MenuItem? = null
        onMake.add {
            println("adding menu item $textRes")
            menuItem = add(groupId, id, order, textRes).apply {
                setIcon(iconRes)
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }.apply(setup)
        }
        onAnimateOutStart.add { a, v ->
            println("removing menu item $textRes")
            removeItem((menuItem ?: return@add).itemId)
        }
    }
}