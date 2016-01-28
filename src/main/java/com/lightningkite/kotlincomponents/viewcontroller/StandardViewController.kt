package com.lightningkite.kotlincomponents.viewcontroller

import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlincomponents.Disposable
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContainer
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCView
import java.util.*

/**
 * Created by jivie on 1/19/16.
 */
abstract class StandardViewController() : ViewController {

    val onMake: ArrayList<(View) -> Unit> = ArrayList()
    val onUnmake: ArrayList<(View) -> Unit> = ArrayList()
    val onDispose: ArrayList<() -> Unit> = ArrayList()

    /**
     * Adds the item to the collection immediately, but removes it when [unmake] is called.
     * The primary use of this is binding things in [make] that need to be removed when [unmake] is called.
     */
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
        onMake.forEach { it(view) }
        onMake.clear()
        return view
    }

    override fun unmake(view: View) {
        onUnmake.forEach { it(view) }
        onUnmake.clear()
        super.unmake(view)
    }

    override fun dispose() {
        onDispose.forEach { it() }
        onDispose.clear()
        super.dispose()
    }

    fun <T : Disposable> autoDispose(vc: T): T {
        onDispose.add { vc.dispose() }
        return vc
    }

    inline fun ViewGroup.viewContainer(container: VCContainer): VCView {
        val vcview = VCView(context as VCActivity)
        vcview.attach(container)
        onUnmake.add {
            vcview.detatch()
        }
        addView(vcview)
        return vcview
    }

    inline fun ViewGroup.viewContainer(container: VCContainer, init: VCView.() -> Unit): VCView {
        val vcview = VCView(context as VCActivity)
        vcview.attach(container)
        onUnmake.add {
            vcview.detatch()
        }
        addView(vcview)
        vcview.init()
        return vcview
    }

}