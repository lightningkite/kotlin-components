package com.lightningkite.kotlincomponents.observable

import java.util.*

/**
 * Created by jivie on 2/22/16.
 */
class KObservableMapped<T, A>(val actualObservable: KObservableInterface<A>, val mapper: (A) -> T, val reverseMapper: (T) -> A) : KObservableInterface<T> {

    val actionToWrapper = HashMap<(T) -> Unit, Wrapper>()

    inner class Wrapper(val func: (T) -> Unit) : (A) -> Unit {
        override fun invoke(p1: A) {
            func(mapper(p1))
        }
    }

    val notPartOfWrapper by lazy { IllegalArgumentException("Function not a wrapper in this KObservableMapped.") }

    override val size: Int get() = actualObservable.size
    override fun contains(element: (T) -> Unit): Boolean =
            actualObservable.contains(actionToWrapper[element] ?: throw notPartOfWrapper)

    override fun containsAll(elements: Collection<(T) -> Unit>): Boolean = actualObservable.containsAll(
            elements.map { actionToWrapper[it] ?: throw notPartOfWrapper }
    )

    override fun isEmpty(): Boolean = actualObservable.isEmpty()
    override fun clear() {
        actualObservable.clear()
    }

    override fun iterator(): MutableIterator<(T) -> Unit> = actionToWrapper.keys.iterator()
    override fun remove(element: (T) -> Unit): Boolean {
        val wrapper = actionToWrapper[element] ?: throw notPartOfWrapper
        actionToWrapper.remove(wrapper.func)
        return actualObservable.remove(wrapper)
    }

    override fun removeAll(elements: Collection<(T) -> Unit>): Boolean = actualObservable.removeAll(
            elements.map {
                val wrapper = actionToWrapper[it] ?: throw notPartOfWrapper
                actionToWrapper.remove(wrapper.func)
                wrapper
            }
    )

    override fun retainAll(elements: Collection<(T) -> Unit>): Boolean = throw UnsupportedOperationException()

    override fun add(element: (T) -> Unit): Boolean {
        element(mapper(actualObservable.get()))
        val wrapper = Wrapper(element)
        actionToWrapper[element] = wrapper
        return actualObservable.add(wrapper)
    }

    override fun addAll(elements: Collection<(T) -> Unit>): Boolean {
        val value = mapper(actualObservable.get())
        elements.forEach { it(value) }

        return actualObservable.addAll(elements.map {
            val wrapper = Wrapper(it)
            actionToWrapper[it] = wrapper
            wrapper
        })
    }

    override fun get(): T = mapper(actualObservable.get())

    override fun set(v: T) {
        actualObservable.set(reverseMapper(v))
    }

    override fun update() = actualObservable.update()
}

inline fun <T, A> KObservableInterface<A>.map(noinline mapper: (A) -> T, noinline reverseMapper: (T) -> A): KObservableMapped<T, A> {
    return KObservableMapped(this, mapper, reverseMapper)
}

inline fun <T> KObservableInterface<T?>.notNull(default: T): KObservableMapped<T, T?> {
    return KObservableMapped(this, { it ?: default }, { it })
}