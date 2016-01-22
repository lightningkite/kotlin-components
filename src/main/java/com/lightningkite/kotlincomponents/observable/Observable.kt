package com.lightningkite.kotlincomponents.observable

import java.util.*
import kotlin.reflect.KProperty

/**
 * Created by jivie on 1/19/16.
 */
open class Observable<T>(
        var value: T
) : ArrayList<(T) -> Unit>() {

    override fun add(element: (T) -> Unit): Boolean {
        element(value)
        return super.add(element)
    }

    override fun addAll(elements: Collection<(T) -> Unit>): Boolean {
        elements.forEach { it(value) }
        return super.addAll(elements)
    }

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
        return value
    }

    /**
     * Sets the value contained by the bond.
     */
    open public fun set(v: T) {
        value = v
        update()
    }

    fun update() {
        for (listener in this) {
            listener(value)
        }
    }
}