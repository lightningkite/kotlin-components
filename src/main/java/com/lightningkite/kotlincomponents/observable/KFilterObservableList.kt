package com.lightningkite.kotlincomponents.observable

import com.lightningkite.kotlincomponents.Disposable
import com.lightningkite.kotlincomponents.runAll
import java.util.*

/**
 * Allows you to observe the changes to a list.
 * Created by josep on 9/7/2015.
 */
class KFilterObservableList<E>(
        val full: KObservableListInterface<E>
) : KObservableListInterface<E>, Disposable {

    val filterObs = KObservable<(E) -> Boolean>({ true })
    var filter by filterObs

    //binding
    val bindings = ArrayList<Pair<MutableCollection<*>, *>>()

    fun <T> bind(collection: MutableCollection<T>, element: T) {
        bindings.add(collection to element)
        collection.add(element)
    }

    override fun dispose() {
        for ((collection, element) in bindings) {
            collection.remove(element)
        }
    }

    //filtering
    var passing = (full.indices).toSortedSet()

    init {
        filterObs.add {
            if (full.none(filter)) {
                passing.clear()
                onReplace.runAll(this)
            } else {
                var otherIndex = 0
                for (i in full.indices) {
                    val passes = filter(full[i])
                    if (passes) {
                        if (passing.add(i)) {
                            onAdd.runAll(full[i], otherIndex)
                        }
                        otherIndex++
                    } else {
                        if (passing.remove(i)) {
                            onRemove.runAll(full[i], otherIndex)
                        }
                    }
                }
            }
            onUpdate.runAll(this)
        }
        bind(full.onAdd) { item, index ->
            val passes = filter(item)
            if (passes) {
                passing.add(index)
                val iterator = passing.iterator()
                val toAdd = ArrayList<Int>()
                while (iterator.hasNext()) {
                    val i = iterator.next()
                    if (i > index) {
                        iterator.remove()
                        toAdd += i + 1
                    }
                }
                passing.addAll(toAdd)
                onAdd.runAll(item, passing.indexOf(index))
            }
        }
        bind(full.onChange) { item, index ->
            val passes = filter(item)
            val passed = passing.contains(index)
            if (passes != passed) {
                if (passes) {
                    passing.add(index)
                    onAdd.runAll(item, index)
                } else {
                    passing.remove(index)
                    onRemove.runAll(item, index)
                }
            }
        }
        bind(full.onRemove) { item, index ->
            val passes = filter(item)
            if (passes) {
                passing.remove(index)
                val iterator = passing.iterator()
                val toAdd = ArrayList<Int>()
                while (iterator.hasNext()) {
                    val i = iterator.next()
                    if (i > index) {
                        iterator.remove()
                        toAdd += i - 1
                    }
                }
                passing.addAll(toAdd)
                onRemove.runAll(item, passing.indexOf(index))
            }
        }
        bind(full.onReplace) {
            passing.clear()
            for (i in full.indices) {
                val passes = filter(full[i])
                if (passes) passing.add(i)
            }
            onReplace.runAll(this)
            onUpdate.runAll(this)
        }
    }

    override val onAdd = HashSet<(E, Int) -> Unit>()
    override val onChange = HashSet<(E, Int) -> Unit>()
    override val onUpdate = KObservableReference<KObservableListInterface<E>>({ this@KFilterObservableList }, { throw IllegalAccessException() })
    override val onReplace = HashSet<(KObservableListInterface<E>) -> Unit>()
    override val onRemove = HashSet<(E, Int) -> Unit>()

    override fun set(index: Int, element: E): E = throw IllegalAccessException()
    override fun add(element: E): Boolean = throw IllegalAccessException()
    override fun add(index: Int, element: E): Unit = throw IllegalAccessException()
    override fun addAll(elements: Collection<E>): Boolean = throw IllegalAccessException()
    override fun addAll(index: Int, elements: Collection<E>): Boolean = throw IllegalAccessException()
    @Suppress("UNCHECKED_CAST")
    override fun remove(element: E): Boolean = throw IllegalAccessException()

    override fun removeAt(index: Int): E = throw IllegalAccessException()
    @Suppress("UNCHECKED_CAST")
    override fun removeAll(elements: Collection<E>): Boolean = throw IllegalAccessException()

    override fun retainAll(elements: Collection<E>): Boolean = throw IllegalAccessException()
    override fun clear(): Unit = throw IllegalAccessException()

    override fun isEmpty(): Boolean = passing.isEmpty()
    override fun contains(element: E): Boolean = passing.contains(full.indexOf(element))
    override fun containsAll(elements: Collection<E>): Boolean = passing.containsAll(elements.map { full.indexOf(it) })
    override fun listIterator(): MutableListIterator<E> = throw UnsupportedOperationException()
    override fun listIterator(index: Int): MutableListIterator<E> = throw UnsupportedOperationException()
    override fun iterator(): MutableIterator<E> = object : MutableIterator<E> {
        var iterator = passing.iterator()
        var current = -1
        override fun hasNext(): Boolean = iterator.hasNext()
        override fun next(): E {
            current = iterator.next()
            return full[current]
        }

        override fun remove() {
            iterator.remove()
            full.removeAt(current)
        }
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = throw UnsupportedOperationException()
    override fun get(index: Int): E = full[passing.elementAt(index)]
    override fun indexOf(element: E): Int = passing.indexOf(full.indexOf(element))
    override fun lastIndexOf(element: E): Int = passing.lastIndexOf(full.lastIndexOf(element))
    override val size: Int get() = passing.size
}