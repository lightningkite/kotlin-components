//package com.lightningkite.kotlincomponents.observable
//
//import java.util.*
//
///**
// * Created by jivie on 5/6/16.
// */
//class KObservableListReference<T>(var source:KObservableListInterface<T>): KObservableListInterface<T>{
//    override val size: Int get() = source.size
//
//    override fun contains(element: T): Boolean = source.contains(element)
//    override fun containsAll(elements: Collection<T>): Boolean = source.containsAll(elements)
//    override fun get(index: Int): T = source.get(index)
//    override fun indexOf(element: T): Int = source.indexOf(element)
//    override fun isEmpty(): Boolean = source.isEmpty()
//    override fun lastIndexOf(element: T): Int = source.lastIndexOf(element)
//    override fun add(element: T): Boolean = source.add(element)
//    override fun add(index: Int, element: T) = source.add(index, element)
//    override fun addAll(index: Int, elements: Collection<T>): Boolean = source.addAll(index, elements)
//    override fun addAll(elements: Collection<T>): Boolean = source.addAll(elements)
//    override fun clear() = source.clear()
//    override fun listIterator(): MutableListIterator<T> = source.listIterator()
//    override fun listIterator(index: Int): MutableListIterator<T> = source.listIterator(index)
//    override fun remove(element: T): Boolean = source.remove(element)
//    override fun removeAll(elements: Collection<T>): Boolean = source.removeAll(elements)
//    override fun removeAt(index: Int): T = source.removeAt(index)
//    override fun retainAll(elements: Collection<T>): Boolean = source.retainAll(elements)
//    override fun set(index: Int, element: T): T = source.set(index, element)
//    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> = source.subList(fromIndex, toIndex)
//    override fun iterator(): MutableIterator<T> = source.iterator()
//    override fun replace(list: List<T>) = source.replace(list)
//
//    override val onAdd: MutableSet<(T, Int) -> Unit> = HashSet()
//    override val onChange: MutableSet<(T, Int) -> Unit> = HashSet()
//    override val onUpdate = KObservableReference<KObservableListInterface<T>>({ this@KObservableListReference }, { replace(it) })
//    override val onReplace: MutableSet<(KObservableListInterface<T>) -> Unit> = HashSet()
//    override val onRemove: MutableSet<(T, Int) -> Unit> = HashSet()
//
//
//}