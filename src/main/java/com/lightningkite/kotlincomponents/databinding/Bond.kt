package com.lightningkite.kotlincomponents.databinding

import android.content.Context
import android.view.View
import com.lightningkite.kotlincomponents.viewcontroller.AutocleanViewController
import com.lightningkite.kotlincomponents.viewcontroller.ViewControllerStack
import java.util.*

/**
 * Created by jivie on 6/25/15.
 */

public open class Bond<T : Any?>(init: T) : AutocleanViewController.Listener {
    protected var listeners: ArrayList<(v: T) -> Unit> = ArrayList()
    protected var myValue: T = init

    public fun get(thisRef: Any?, prop: PropertyMetadata): T {
        return get()
    }

    open public fun get(): T {
        return myValue
    }

    public fun set(thisRef: Any?, prop: PropertyMetadata, v: T) {
        set(v)
    }

    open public fun set(v: T) {
        myValue = v
        update()
    }

    public open fun update() {
        for (listener in listeners) {
            listener(myValue)
        }
    }

    public fun bind(body: (v: T) -> Unit) {
        listeners.add(body)
        body(myValue)
    }

    public fun unbind(body: (v: T) -> Unit) {
        listeners.remove(body)
    }

    public fun clearBindings() {
        listeners.clear()
    }

    override fun make(context: Context, stack: ViewControllerStack) {
    }

    override fun unmake(view: View) {
        clearBindings()
    }

    override fun dispose() {
    }

    override fun toString(): String {
        return "Bond(" + myValue.toString() + ")"
    }
}