package com.lightningkite.kotlincomponents.observable

/**
 * Created by jivie on 5/18/16.
 */
open class ItemChange<T>(
        var old: T? = null,
        var new: T? = null
) {
    var timeStamp: Long = System.currentTimeMillis()

    val isAdd: Boolean get() = old == null && new != null
    val isRemove: Boolean get() = old != null && new == null
    val isClear: Boolean get() = old == null && new == null
    val isChange: Boolean get() = old != null && new != null

    override fun toString(): String {
        return "ItemChange(old=$old, new=$new)"
    }
}