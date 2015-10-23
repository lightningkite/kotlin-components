package com.lightningkite.kotlincomponents.databinding

import android.view.View
import com.lightningkite.kotlincomponents.viewcontroller.AutocleanViewController
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import java.util.*

/**
 * This class is used for binding data to views.
 * Created by jivie on 6/25/15.
 */
public open class Bond<T : Any?>(init: T) : AutocleanViewController.Listener {
    protected var listeners: ArrayList<(v: T) -> Unit> = ArrayList()
    protected var myValue: T = init

    operator public fun getValue(thisRef: Any?, prop: PropertyMetadata): T {
        return get()
    }

    operator public fun setValue(thisRef: Any?, prop: PropertyMetadata, v: T) {
        set(v)
    }

    /**
     * Gets the value contained by the bond.
     */
    open public fun get(): T {
        return myValue
    }

    /**
     * Sets the value contained by the bond.
     */
    open public fun set(v: T) {
        myValue = v
        update()
    }

    /**
     * Calls all of the listeners.
     */
    public open fun update() {
        for (listener in listeners) {
            listener(myValue)
        }
    }

    /**
     * Adds a new listener for changes to the value.
     * Called immediately with the current value.
     */
    public fun bind(body: (v: T) -> Unit) {
        listeners.add(body)
        body(myValue)
    }

    /**
     * Removes the listener.
     */
    public fun unbind(body: (v: T) -> Unit) {
        listeners.remove(body)
    }

    /**
     * Clears all of the listeners.
     */
    public fun clearBindings() {
        listeners.clear()
    }

    override fun make(activity: VCActivity) {
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