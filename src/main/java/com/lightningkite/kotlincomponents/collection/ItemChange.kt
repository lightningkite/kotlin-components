package com.lightningkite.kotlincomponents.collection

/**
 * Created by jivie on 3/29/16.
 */
class ItemChange<T>(var old: T? = null, var new: T? = null) {
    val isAdd: Boolean get() = old == null && new != null
    val isRemove: Boolean get() = old != null && new == null
    val isClear: Boolean get() = old == null && new == null
    val isChange: Boolean get() = old != null && new != null
}