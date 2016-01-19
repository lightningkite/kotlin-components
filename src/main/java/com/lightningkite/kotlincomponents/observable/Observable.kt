package com.lightningkite.kotlincomponents.observable

import java.util.*
import kotlin.reflect.KProperty

/**
 * Created by jivie on 1/19/16.
 */
open class Observable<T>(
        var value: T,
        val listeners: ArrayList<(T) -> Unit> = ArrayList()
) : MutableCollection<(T) -> Unit> {

    override fun clear() = listeners.clear()
    override fun remove(element: (T) -> Unit): Boolean = listeners.remove(element)
    override fun removeAll(elements: Collection<(T) -> Unit>): Boolean = listeners.removeAll(elements)
    override fun retainAll(elements: Collection<(T) -> Unit>): Boolean = listeners.retainAll(elements)
    override val size: Int = listeners.size
    override fun contains(element: (T) -> Unit): Boolean = listeners.contains(element)
    override fun containsAll(elements: Collection<(T) -> Unit>): Boolean = listeners.containsAll(elements)
    override fun isEmpty(): Boolean = listeners.isEmpty()
    override fun iterator(): MutableIterator<(T) -> Unit> = listeners.iterator()

    override fun add(element: (T) -> Unit): Boolean {
        element(value)
        return listeners.add(element)
    }

    override fun addAll(elements: Collection<(T) -> Unit>): Boolean {
        elements.forEach { it(value) }
        return listeners.addAll(elements)
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
        for (listener in listeners) {
            listener(value)
        }
    }
}