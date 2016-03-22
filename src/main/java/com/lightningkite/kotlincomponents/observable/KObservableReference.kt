package com.lightningkite.kotlincomponents.observable

import java.util.*
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1

/**
 * Created by jivie on 2/22/16.
 */
class KObservableReference<T>(val getter: () -> T, val setter: (T) -> Unit) : KObservableInterface<T> {

    val list = ArrayList<(T) -> Unit>()
    override val size: Int get() = list.size
    override fun contains(element: (T) -> Unit): Boolean = list.contains(element)
    override fun containsAll(elements: Collection<(T) -> Unit>): Boolean = list.containsAll(elements)
    override fun isEmpty(): Boolean = list.isEmpty()
    override fun clear() {
        list.clear()
    }

    override fun iterator(): MutableIterator<(T) -> Unit> = list.iterator()
    override fun remove(element: (T) -> Unit): Boolean {
        val result = list.remove(element)
        println("$this-$size")
        return result
    }
    override fun removeAll(elements: Collection<(T) -> Unit>): Boolean = list.removeAll(elements)
    override fun retainAll(elements: Collection<(T) -> Unit>): Boolean = list.retainAll(elements)

    override fun add(element: (T) -> Unit): Boolean {
        element(getter())
        val result = list.add(element)
        println("$this-$size")
        return result
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

fun <T> KMutableProperty0<T>.toKObservableReference(): KObservableReference<T> {
    return KObservableReference({ get() }, { set(it) })
}

fun <T, R> KMutableProperty1<R, T>.toKObservableReference(receiver: R): KObservableReference<T> {
    return KObservableReference({ get(receiver) }, { set(receiver, it) })
}