package com.lightningkite.kotlincomponents.observable

import com.lightningkite.kotlincomponents.runAll
import java.util.*

/**
 * Created by jivie on 1/19/16.
 */
open class KObservable<T>(
        var value: T
) : KObservableInterface<T> {

    val list = ArrayList<(T) -> Unit>()
    override val size: Int get() = list.size
    override fun contains(element: (T) -> Unit): Boolean  = list.contains(element)
    override fun containsAll(elements: Collection<(T) -> Unit>): Boolean = list.containsAll(elements)
    override fun isEmpty(): Boolean = list.isEmpty()
    override fun clear(){list.clear()}
    override fun iterator(): MutableIterator<(T) -> Unit> =list.iterator()
    override fun remove(element: (T) -> Unit): Boolean {
        val result = list.remove(element)
        return result
    }
    override fun removeAll(elements: Collection<(T) -> Unit>): Boolean = list.removeAll(elements)
    override fun retainAll(elements: Collection<(T) -> Unit>): Boolean = list.retainAll(elements)
    override fun add(element: (T) -> Unit): Boolean {
        val result = list.add(element)
        return result
    }
    override fun addAll(elements: Collection<(T) -> Unit>): Boolean = list.addAll(elements)

    override fun get(): T {
        return value
    }

    override fun set(v: T) {
        value = v
        update()
    }

    override fun update() {
        list.runAll(value)
    }
}