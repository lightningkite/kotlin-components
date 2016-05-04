package com.lightningkite.kotlincomponents.sync

/**
 * Created by jivie on 4/7/16.
 */
interface KSyncedListItem<T : KSyncedListItem<T, K>, K : Any> : Mergeable<K, T>, Syncable {
    //NEEDS TO BE TRANSIENT
    var parent: KSyncedList<T, K>?

    fun isSynced(): Boolean {
        return parent?.changes?.get(getKey()) == null
    }

    fun hasFailingChange(): Boolean {
        return parent?.changes?.get(getKey())?.error != null
    }
}