package com.lightningkite.kotlincomponents.adapter

import com.lightningkite.kotlincomponents.observable.KObservableInterface
import java.util.*

/**
 * Created by jivie on 2/11/16.
 */
class ItemObservable<T : Any>() : KObservableInterface<T> {

    constructor(initValue: T) : this() {
        set(initValue)
    }

    var index: Int = 0

    var value: T? = null

    val list = ArrayList<(T) -> Unit>()
    override val size: Int = list.size
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
        if (value != null) element(value!!)
        return list.add(element)
    }

    override fun addAll(elements: Collection<(T) -> Unit>): Boolean {
        if (value != null) elements.forEach { it(value!!) }
        return list.addAll(elements)
    }

    override fun get(): T {
        if (value == null) throw IllegalAccessException("Value not set.")
        return value!!
    }

    override fun set(v: T) {
        value = v
        update()
    }

    override fun update() {
        if (value != null) {
            for (listener in list) {
                listener(value!!)
            }
        }
    }
}