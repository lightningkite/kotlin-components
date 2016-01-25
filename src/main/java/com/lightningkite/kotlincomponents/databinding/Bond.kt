package com.lightningkite.kotlincomponents.databinding

import android.view.View
import com.lightningkite.kotlincomponents.viewcontroller.AutocleanViewController
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import java.util.*
import kotlin.reflect.KProperty

/**
 * This class is used for binding data to views.
 * Created by jivie on 6/25/15.
 */
@Deprecated("This has been replaced with a better-design class KObservable.")
public open class Bond<T : Any?>(init: T) : AutocleanViewController.Listener {
    protected var listeners: ArrayList<(v: T) -> Unit> = ArrayList()
    protected var myValue: T = init

    operator public fun getValue(thisRef: Any?, prop: KProperty<*>): T {
        return get()
    }

    operator public fun setValue(thisRef: Any?, prop: KProperty<*>, v: T) {
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

    companion object {
        fun <A> bind(first: Bond<A>, action: () -> Unit) {
            first.bind { action() }
        }

        //Bind an action to two bonds.
        fun <A, B> bind(first: Bond<A>, second: Bond<B>, action: () -> Unit) {
            first.bind { action() }
            second.bind { action() }
        }

        //Bind an action to three bonds.
        fun <A, B, C> bind(first: Bond<A>, second: Bond<B>, third: Bond<C>, action: () -> Unit) {
            first.bind { action() }
            second.bind { action() }
            third.bind { action() }
        }
    }
}