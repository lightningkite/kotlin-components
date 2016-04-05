package com.lightningkite.kotlincomponents.collection

import java.util.*

/**
 * Created by jivie on 4/4/16.
 */
inline fun <K, T> MutableList<T>.merge(newList: List<T>, crossinline getKey: (T) -> K, crossinline merge: (T, T) -> T) {
    var toDelete = HashSet<K>(this.map { getKey(it) })
    for (new in newList) {
        toDelete.remove(getKey(new))
        val index = this.indexOfFirst { getKey(it) == getKey(new) }
        if (index == -1) {
            //new item from server
            this.add(new)
        } else {
            //updated item from server
            this[index] = merge(this[index], new)
        }
    }
    this.removeAll { toDelete.contains(getKey(it)) }
}

fun <K : Any, T : Mergeable<K, T>> MutableList<T>.merge(newList: List<T>) {
    var toDelete = HashSet<K>(this.map { it.getKey() })
    for (new in newList) {
        toDelete.remove(new.getKey())
        val index = this.indexOfFirst { it.getKey() == new.getKey() }
        if (index == -1) {
            //new item from server
            this.add(new)
        } else {
            //updated item from server
            this[index].merge(new)
        }
    }
    this.removeAll { toDelete.contains(it.getKey()) }
}

inline fun MutableList<*>.addUntyped(e: Any) {
    (this as MutableList<Any>).add(e)
}

fun MutableList<*>.mergeUntyped(newList: List<*>) {
    var toDelete = HashSet<Any>(this.map { (it as Mergeable<*, *>).getKeyUntyped() })
    for (untypedNew in newList) {
        val new = (untypedNew as Mergeable<*, *>)
        toDelete.remove(new.getKey())
        val index = this.indexOfFirst { (it as Mergeable<*, *>).getKeyUntyped() == new.getKeyUntyped() }
        if (index == -1) {
            //new item from server
            this.addUntyped(new)
        } else {
            //updated item from server
            (this[index] as Mergeable<*, *>).mergeUntyped(new)
        }
    }
    this.removeAll { toDelete.contains((it as Mergeable<*, *>).getKeyUntyped()) }
}