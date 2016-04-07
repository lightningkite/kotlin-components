package com.lightningkite.kotlincomponents.sync

/**
 * Created by jivie on 4/7/16.
 */
interface KSyncedListItem<T : KSyncedListItem<T, K>, K : Any> : Mergeable<K, T>, Syncable {
    //NEEDS TO BE TRANSIENT
    var parent: KSyncedList<T, K>?
}