package com.lightningkite.kotlincomponents.observable

import java.util.*
import kotlin.reflect.KProperty

/**
 * Created by jivie on 1/19/16.
 */
open class KObservable<T>(
        var value: T
) : MutableCollection<(T) -> Unit> {

    val list = ArrayList<(T) -> Unit>()
    override val size: Int = list.size
    override fun contains(element: (T) -> Unit): Boolean  = list.contains(element)
    override fun containsAll(elements: Collection<(T) -> Unit>): Boolean = list.containsAll(elements)
    override fun isEmpty(): Boolean = list.isEmpty()
    override fun clear(){list.clear()}
    override fun iterator(): MutableIterator<(T) -> Unit> =list.iterator()
    override fun remove(element: (T) -> Unit): Boolean = list.remove(element)
    override fun removeAll(elements: Collection<(T) -> Unit>): Boolean = list.removeAll(elements)
    override fun retainAll(elements: Collection<(T) -> Unit>): Boolean = list.retainAll(elements)

    override fun add(element: (T) -> Unit): Boolean {
        element(value)
        return list.add(element)
    }

    override fun addAll(elements: Collection<(T) -> Unit>): Boolean {
        elements.forEach { it(value) }
        return list.addAll(elements)
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
        for (listener in list) {
            listener(value)
        }
    }
}