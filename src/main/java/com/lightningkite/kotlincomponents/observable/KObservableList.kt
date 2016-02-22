package com.lightningkite.kotlincomponents.observable

import java.util.*

/**
 * Allows you to observe the changes to a list.
 * Created by josep on 9/7/2015.
 */
class KObservableList<E>(
        val collection: MutableList<E>
) : KObservableListInterface<E> {

    override val onAdd = HashSet<(E, Int) -> Unit>()
    override val onChange = HashSet<(E, Int) -> Unit>()
    override val onUpdate = HashSet<(KObservableList<E>) -> Unit>()
    override val onRemove = HashSet<(E, Int) -> Unit>()

    override fun set(index: Int, element: E): E {
        collection[index] = element
        onChange.forEach { it(element, index) }
        onUpdate.forEach { it(this) }
        return element
    }

    override fun add(element: E): Boolean {
        val result = collection.add(element)
        val index = collection.size - 1
        if (result) {
            onAdd.forEach { it(element, index) }
            onUpdate.forEach { it(this) }
        }
        return result
    }

    override fun add(index: Int, element: E) {
        collection.add(index, element)
        onAdd.forEach { it(element, index) }
        onUpdate.forEach { it(this) }
    }

    override fun addAll(elements: Collection<E>): Boolean {
        var index = collection.size
        collection.addAll(elements)
        for (e in elements) {
            onAdd.forEach { it(e, index) }
            index++
        }
        onUpdate.forEach { it(this) }
        return true
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        collection.addAll(elements)
        var currentIndex = index
        for (e in elements) {
            onAdd.forEach { it(e, currentIndex) }
            currentIndex++
        }
        onUpdate.forEach { it(this) }
        return true
    }

    @Suppress("UNCHECKED_CAST")
    override fun remove(element: E): Boolean {
        val index = indexOf(element)
        if (index == -1) return false
        collection.removeAt(index)
        onRemove.forEach { it(element, index) }
        onUpdate.forEach { it(this) }
        return true
    }

    override fun removeAt(index: Int): E {
        val element = collection.removeAt(index)
        onRemove.forEach { it(element, index) }
        onUpdate.forEach { it(this) }
        return element
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeAll(elements: Collection<E>): Boolean {
        for (e in elements) {
            remove(e)
        }
        return true
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun clear() {
        collection.forEachIndexed { i, e ->
            onRemove.forEach { it(e, i) }
        }
        collection.clear()
        onUpdate.forEach { it(this) }
    }

    override fun isEmpty(): Boolean = collection.isEmpty()
    override fun contains(element: E): Boolean = collection.contains(element)
    override fun containsAll(elements: Collection<E>): Boolean = collection.containsAll(elements)
    override fun listIterator(): MutableListIterator<E> = throw UnsupportedOperationException()
    override fun listIterator(index: Int): MutableListIterator<E> = throw UnsupportedOperationException()
    override fun iterator(): MutableIterator<E> = collection.iterator()
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = collection.subList(fromIndex, toIndex)
    override fun get(index: Int): E = collection[index]
    override fun indexOf(element: E): Int = collection.indexOf(element)
    override fun lastIndexOf(element: E): Int = collection.lastIndexOf(element)
    override val size: Int get() = collection.size

}