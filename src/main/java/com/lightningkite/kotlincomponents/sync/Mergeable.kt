package com.lightningkite.kotlincomponents.sync

/**
 * Created by jivie on 4/4/16.
 */
interface Mergeable<K : Any, T : Any> {
    fun getKey(): K
    fun getKeyUntyped(): Any = getKey()
    fun merge(other: T)
    fun mergeUntyped(other: Any) = merge(other as T)
}