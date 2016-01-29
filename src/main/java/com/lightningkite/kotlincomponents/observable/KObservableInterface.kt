package com.lightningkite.kotlincomponents.observable

import kotlin.reflect.KProperty

/**
 * Created by josep on 1/28/2016.
 */
interface KObservableInterface<T> : MutableCollection<(T) -> Unit> {
    operator public fun getValue(thisRef: Any?, prop: KProperty<*>): T {
        return get()
    }

    operator public fun setValue(thisRef: Any?, prop: KProperty<*>, v: T) {
        set(v)
    }

    /**
     * Gets the value contained by the bond.
     */
    public fun get(): T

    /**
     * Sets the value contained by the bond.
     */
    public fun set(v: T)

    /**
     * Calls all of the listeners.
     */
    public fun update()
}