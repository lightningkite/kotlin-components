package com.lightningkite.kotlincomponents.observable

import java.util.*

/**
 * Created by jivie on 2/22/16.
 */
abstract class KObservableBuffered<T>() : KObservableInterface<T> {

    abstract fun getter(): T
    abstract fun setter(value: T)

    val list = ArrayList<(T) -> Unit>()
    override val size: Int get() = list.size
    override fun contains(element: (T) -> Unit): Boolean = list.contains(element)
    override fun containsAll(elements: Collection<(T) -> Unit>): Boolean = list.containsAll(elements)
    override fun isEmpty(): Boolean = list.isEmpty()
    override fun clear() {
        list.clear()
    }

    override fun iterator(): MutableIterator<(T) -> Unit> = list.iterator()
    override fun remove(element: (T) -> Unit): Boolean = list.remove(element)
    override fun removeAll(elements: Collection<(T) -> Unit>): Boolean = list.removeAll(elements)
    override fun retainAll(elements: Collection<(T) -> Unit>): Boolean = list.retainAll(elements)

    override fun add(element: (T) -> Unit): Boolean {
        element(getter())
        return list.add(element)
    }

    override fun addAll(elements: Collection<(T) -> Unit>): Boolean {
        val value = getter()
        elements.forEach { it(value) }
        return list.addAll(elements)
    }

    override fun get(): T = getter()

    override fun set(v: T) {
        setter(v)
        update()
    }

    override fun update() {
        val value = getter()
        for (listener in list) {
            listener(value)
        }
    }
}