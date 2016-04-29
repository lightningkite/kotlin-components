package com.lightningkite.kotlincomponents.sync

/**
 * Created by jivie on 3/29/16.
 */
class ItemChange<T : KSyncedListItem<T, K>, K : Any>(
        var old: T? = null,
        var new: T? = null,
        @Transient var belongsTo: KSyncedList<T, K>? = null,
        var error: SyncError? = null
) {
    val isAdd: Boolean get() = old == null && new != null
    val isRemove: Boolean get() = old != null && new == null
    val isClear: Boolean get() = old == null && new == null
    val isChange: Boolean get() = old != null && new != null
}