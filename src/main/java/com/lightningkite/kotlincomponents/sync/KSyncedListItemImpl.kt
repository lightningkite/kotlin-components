package com.lightningkite.kotlincomponents.sync

import android.util.Log
import com.lightningkite.kotlincomponents.observable.KObservableBase
import com.lightningkite.kotlincomponents.withEachAsync
import java.util.*

/**
 * Created by jivie on 4/7/16.
 */
abstract class KSyncedListItemImpl<THIS : KSyncedListItem<THIS, KEY>, KEY : Any> : KSyncedListItem<THIS, KEY> {

    @Transient val onUpdate = object : KObservableBase<THIS>() {
        @Suppress("UNCHECKED_CAST")
        override fun get(): THIS = this@KSyncedListItemImpl as THIS

        override fun set(v: THIS) {
            throw UnsupportedOperationException()
        }

        @Suppress("UNCHECKED_CAST")
        override fun update() {
            update(this@KSyncedListItemImpl as THIS)
        }

    }
    override @Transient var parent: KSyncedList<THIS, KEY>? = null

    @Transient private val syncables: ArrayList<Syncable> = ArrayList()
    @Transient private val lazySyncables: ArrayList<Lazy<Syncable>> = ArrayList()

    fun <T : Syncable> addSyncable(item: T): T {
        syncables.add(item)
        return item
    }

    fun <T : Syncable> addSyncable(item: Lazy<T>): Lazy<T> {
        lazySyncables.add(item)
        return item
    }

    override fun loadLocal() {
        syncables.forEach { it.loadLocal() }
        lazySyncables.forEach { it.value.loadLocal() }
    }

    @Suppress("UNCHECKED_CAST")
    override fun saveLocal() {
        syncables.forEach { it.saveLocal() }
        lazySyncables.forEach { it.value.saveLocal() }
        onUpdate.update()
        parent?.update(this as THIS)
    }

    override fun sync(onComplete: () -> Unit) {
        Log.i("KSyncedListItemImpl", "Syncing ${syncables.size} syncables.")
        var done = 2
        val onDone = {
            done--
            if (done <= 0)
                onComplete()
        }
        syncables.withEachAsync({ sync(it) }, onDone)
        lazySyncables.withEachAsync({ value.sync(it) }, onDone)
    }
}