package com.lightningkite.kotlincomponents.sync

import com.lightningkite.kotlincomponents.observable.ItemChange

/**
 * Created by jivie on 3/29/16.
 */
class KSyncedItemListChange<T : KSyncedListItem<T, K>, K : Any>(
        old: T? = null,
        new: T? = null,
        @Transient var belongsTo: KSyncedList<T, K>? = null,
        var error: SyncError? = null
) : ItemChange<T>(old, new) {
    fun getKey(): K? = old?.getKey() ?: new?.getKey()

    override fun toString(): String {
        return "ItemChange(old=$old, new=$new)"
    }
}