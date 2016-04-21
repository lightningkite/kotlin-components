package com.lightningkite.kotlincomponents.sync

import android.util.Log
import com.github.salomonbrys.kotson.typeToken
import com.lightningkite.kotlincomponents.async.doAsync
import com.lightningkite.kotlincomponents.async.doUiThread
import com.lightningkite.kotlincomponents.files.child
import com.lightningkite.kotlincomponents.files.load
import com.lightningkite.kotlincomponents.files.save
import com.lightningkite.kotlincomponents.gsonTo
import com.lightningkite.kotlincomponents.networking.NetEndpoint
import com.lightningkite.kotlincomponents.networking.NetMethod
import com.lightningkite.kotlincomponents.networking.Networking
import com.lightningkite.kotlincomponents.networking.sync
import com.lightningkite.kotlincomponents.observable.KObservableBase
import com.lightningkite.kotlincomponents.observable.KObservableInterface
import com.lightningkite.kotlincomponents.observable.KObservableList
import com.lightningkite.kotlincomponents.observable.KObservableListInterface
import com.lightningkite.kotlincomponents.withReduceAsync
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * Created by jivie on 3/29/16.
 */

inline fun <reified T : KSyncedListItem<T, K>, reified K : Any> defaultSyncPush(
        endpoint: NetEndpoint,
        change: ItemChange<T, K>
): SyncError? {
    var error: SyncError? = null
    if (change.isAdd) {
        Log.i("KSyncedList", "Posting new item: ${change.new!!.gsonTo()}")
        endpoint.syncPost<T>(change.new) { error = SyncError(it.string(), it); true }?.let {
            change.new!!.merge(it)
        }
    } else if (change.isChange) {
        Log.i("KSyncedList", "Putting change: ${change.new!!.gsonTo()}")
        endpoint.sub(change.old!!.getKey().toString()).syncPatch<Unit>(change.new) { error = SyncError(it.string(), it); true }
    } else if (change.isRemove) {
        Log.i("KSyncedList", "Deleting item: ${change.old!!.gsonTo()}")
        endpoint.sub(change.old!!.getKey().toString()).syncDelete<Unit>(null) { error = SyncError(it.string(), it); true }
    } else if (change.isClear) {
        Log.i("KSyncedList", "Clearing items.")
        endpoint.syncDelete<Unit>(null) { error = SyncError(it.string(), it); true }
    }
    return error
}

inline fun <reified T : KSyncedListItem<T, K>, reified K : Any> KSyncedList(
        noinline getFolder: () -> File,
        noinline getEndpoint: () -> NetEndpoint
): KSyncedList<T, K> {
    val listType: ParameterizedType = object : ParameterizedType {
        override fun getRawType(): Type? = ArrayList::class.java
        override fun getOwnerType(): Type? = null
        override fun getActualTypeArguments(): Array<out Type>? = arrayOf(typeToken<T>())
    }
    return KSyncedList(
            typeToken<T>(),
            getFolder,
            {
                var result = PullResult<T>(null, null)
                var list: List<T>? = null
                val endpoint = getEndpoint()
                val response = Networking.sync(endpoint.request(NetMethod.GET))
                try {
                    list = response.gson<ArrayList<T>>(listType)
                    if (list == null) {
                        IllegalArgumentException("Could not parse data: $response").printStackTrace()
                        result = PullResult(SyncError("Could not parse data.", response))
                    } else {
                        result = PullResult(list)
                    }
                } catch(e: Exception) {
                    IllegalArgumentException("Could not parse data: $response", e).printStackTrace()
                    result = PullResult(SyncError("Could not parse data.", response))
                }
                result
            },
            syncPush = { change -> defaultSyncPush(getEndpoint(), change) }
    )
}

inline fun <reified T : KSyncedListItem<T, K>, reified K : Any> KSyncedList(
        noinline getFolder: () -> File,
        noinline getEndpoint: () -> NetEndpoint,
        noinline customPull: () -> PullResult<T>
): KSyncedList<T, K> {
    val listType: ParameterizedType = object : ParameterizedType {
        override fun getRawType(): Type? = ArrayList::class.java
        override fun getOwnerType(): Type? = null
        override fun getActualTypeArguments(): Array<out Type>? = arrayOf(typeToken<T>())
    }
    return KSyncedList(
            typeToken<T>(),
            getFolder,
            customPull,
            syncPush = { change -> defaultSyncPush(getEndpoint(), change) }
    )
}

open class KSyncedList<T : KSyncedListItem<T, K>, K : Any>(
        val type: Type,
        val getFolder: () -> File,
        val syncPull: () -> PullResult<T>,
        val syncPush: (ItemChange<T, K>) -> SyncError?,
        val innerList: KObservableListInterface<T> = KObservableList()
) : KObservableListInterface<T> by innerList, Syncable {

    var pushImmediately: Boolean = true

    init {
        innerList.onAdd.add { item, index ->
            item.parent = this
        }
    }

    val changes = HashMap<K?, ItemChange<T, K>>()
    var numChanges = 0

    private var folder: File = getFolder()
    fun updateFolder() {
        val newFolder = getFolder()
        if (folder == newFolder) return
        folder = newFolder
        //move backup and changes file
        if (file.exists()) {
            file.renameTo(newFolder.child("data.json"))
        }
        if (changesFile.exists()) {
            changesFile.renameTo(newFolder.child("changes.json"))
        }
    }

    private val file: File get() {
        return folder.child("data.json")
    }
    private val changesFile: File get() {
        return folder.child("changes.json")
    }

    private val changeType: ParameterizedType = object : ParameterizedType {
        override fun getRawType(): Type? = ItemChange::class.java
        override fun getOwnerType(): Type? = null
        override fun getActualTypeArguments(): Array<out Type>? = arrayOf(type)
    }

    fun observable(key: K): KObservableInterface<T> {
        return object : KObservableBase<T>() {
            override fun get(): T {
                if (isEmpty()) index = this@KSyncedList.indexOfFirst { it.getKey() == key }
                return this@KSyncedList[index]
            }

            override fun set(v: T) {
                this@KSyncedList[index] = v
            }

            var index: Int = this@KSyncedList.indexOfFirst { it.getKey() == key }

            val listener = { it: KObservableListInterface<T> ->
                index = this@KSyncedList.indexOfFirst { it.getKey() == key }
            }

            override fun add(element: (T) -> Unit): Boolean {
                if (isEmpty()) {
                    this@KSyncedList.onUpdate.add(listener)
                }
                return super.add(element)
            }

            override fun remove(element: (T) -> Unit): Boolean {
                if (size <= 1) {
                    this@KSyncedList.onUpdate.remove(listener)
                }
                return super.remove(element)
            }

            override fun update() {
                //This may look redundant, but it's not.  This is very important.  It fires the
                //write command on this list, writing the update to the local cache, ensuring it
                //will make it back to the server eventually.
                this@KSyncedList[index] = this@KSyncedList[index]
                super.update(get())
            }
        }
    }

    override fun sync(onComplete: (List<SyncError>) -> Unit) {
        //online sync
        doAsync {
            val failed = processChanges(syncPush)

            Log.i("KSyncedList", "Pulling new data...")
            val pullResult = syncPull()
            if (pullResult.list != null) {
                doUiThread {
                    Log.i("KSyncedList", "Merging new data of size ${pullResult.list.size}...")
                    innerList.merge(pullResult.list)
                    try {
                        save(file)
                    } catch(e: Exception) {
                        e.printStackTrace()
                    }
                    applyChangesToData(changes.values)
                    this@KSyncedList.withReduceAsync(
                            {
                                Log.i("KSyncedList", "Syncing child ${getKey()}...")
                                sync(it)
                            },
                            ArrayList<SyncError>().apply {
                                for (change in failed) add(change.error ?: SyncError())
                            },
                            { it: List<SyncError> -> addAll(it) },
                            onComplete
                    )
                }
            } else {
                Log.e("KSyncedList", "Failed to pull new data.")
                doUiThread {
                    onComplete(listOf(pullResult.error ?: SyncError("No error data given")))
                }
            }
        }
    }

    override fun loadLocal() {
        //local load
        try {
            load(file, type)

            if (!changesFile.exists()) return
            changes.clear()

            val changesRaw: ArrayList<ItemChange<T, K>> = ArrayList()
            changesRaw.load(changesFile, changeType)

            for (change in changesRaw) {
                //setup change
                change.belongsTo = this

                //add to change map
                changes[change.getKey()] = change
            }

            applyChangesToData(changesRaw)
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    override fun saveLocal() {
        try {
            changesFile.parentFile.mkdirs()
            if (!changesFile.exists()) changesFile.createNewFile()
            FileOutputStream(changesFile, false).bufferedWriter().apply {
                for (change in changes.values) {
                    appendln(change.gsonTo())
                }
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearFailingChanges() {
        if (!changesFile.exists()) return
        val changes: ArrayList<ItemChange<T, K>> = ArrayList()
        try {
            changes.load(changesFile, changeType)
        } catch(e: Exception) {
            e.printStackTrace()
        }
        val notFailedChanges = changes.filter { it.error == null }
        modifyChangeFile(false) {
            numChanges = notFailedChanges.size
            for (change in notFailedChanges) {
                it.appendln(change.gsonTo())
            }
        }
    }

    /**
     * Does something on all of the changes.  The successful changes are deleted.
     **/
    private fun processChanges(forChange: (ItemChange<T, K>) -> SyncError?): List<ItemChange<T, K>> {
        val failedChanges = ArrayList<ItemChange<T, K>>()
        val sortedChanges = changes.values.sortedBy { it.timeStamp }
        for (change in sortedChanges) {
            change.belongsTo = this
            val error = forChange(change)
            if (error != null) {
                change.error = error
                failedChanges.add(change)
            }
        }
        changes.clear()
        for (failed in failedChanges) {
            changes[failed.getKey()] = failed
        }
        saveLocal()
        return failedChanges
    }


    private inline fun applyChangesToData(changes: Collection<ItemChange<T, K>>) {
        for (change in changes) {
            //apply change to local data
            if (change.old == null) {
                if (change.new == null) {
                    //clear
                    innerList.clear()
                } else {
                    //add
                    innerList.add(change.new!!)
                }
            } else {
                if (change.new == null) {
                    //remove
                    val index = indexOfFirst { change.old!!.getKey() == it.getKey() }
                    if (index != -1) {
                        innerList.removeAt(index)
                    }
                } else {
                    //change
                    val index = indexOfFirst { change.old!!.getKey() == it.getKey() }
                    if (index != -1) {
                        innerList[index] = change.new!!
                    }
                }
            }
        }
    }

    fun update(item: T) {
        Log.i("KSyncedList", "Updating item ${item.getKey()}")
        val index = this.indexOfFirst { item.getKey() == it.getKey() }
        this[index].merge(item)
        addChange(ItemChange(item, item))
    }

    fun update(index: Int) {
        val item = this[index]
        Log.i("KSyncedList", "Updating item ${item.getKey()}")
        addChange(ItemChange(item, item))
    }

    override fun add(element: T): Boolean {
        Log.i("KSyncedList", "Adding item ${element.getKey()}")
        element.parent = this
        addChange(ItemChange(null, element))
        return innerList.add(element)
    }

    override fun add(index: Int, element: T) {
        Log.i("KSyncedList", "Adding item ${element.getKey()}")
        element.parent = this
        addChange(ItemChange(null, element))
        innerList.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        for (element in elements) {
            Log.i("KSyncedList", "Adding item ${element.getKey()}")
            element.parent = this
        }
        addChanges(elements.map { ItemChange(null, it) })
        return innerList.addAll(index, elements)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        for (element in elements) {
            Log.i("KSyncedList", "Adding item ${element.getKey()}")
            element.parent = this
        }
        addChanges(elements.map { ItemChange(null, it) })
        return innerList.addAll(elements)
    }

    override fun remove(element: T): Boolean {
        Log.i("KSyncedList", "Removing item ${element.getKey()}")
        element.parent = null
        addChange(ItemChange(element, null))
        return innerList.remove(element)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        for (element in elements) {
            Log.i("KSyncedList", "Removing item ${element.getKey()}")
            element.parent = null
        }
        addChanges(elements.map { ItemChange(it, null) })
        return innerList.removeAll(elements)
    }

    override fun removeAt(index: Int): T {
        Log.i("KSyncedList", "Removing item ${this[index].getKey()}")
        this[index].parent = null
        addChange(ItemChange(this[index], null))
        return innerList.removeAt(index)
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun set(index: Int, element: T): T {
        val previous = this[index]
        Log.i("KSyncedList", "Updating item ${element.getKey()}")
        element.parent = this
        addChange(ItemChange(previous, element))
        return innerList.set(index, element)
    }

    override fun clear() {
        innerList.clear()
        addChange(ItemChange(null, null))
    }

    override fun replace(list: List<T>) {
        clear()
        addAll(list)
    }

    private inline fun addChange(change: ItemChange<T, K>) {
        if (pushImmediately) {
            doAsync {
                val error = syncPush(change)
                if (error != null) {
                    Log.e("KSyncedList", "Error pushing change: ${error.toString()}")
                    storeChange(change)
                    saveLocal()
                }
            }
        } else {
            storeChange(change)
            saveLocal()
        }
    }

    private inline fun addChanges(changes: List<ItemChange<T, K>>) {
        if (pushImmediately) {
            for (change in changes)
                addChange(change)
        } else {
            for (change in changes) {
                storeChange(change)
            }
            saveLocal()
        }
    }

    private inline fun storeChange(change: ItemChange<T, K>) {
        val currentChange = changes[change.getKey()]
        if (currentChange == null) {
            changes[change.getKey()] = change
        } else {
            if (change.isRemove) {
                if (currentChange.isAdd) {
                    //removing unpushed addition - just remove it!
                    changes.remove(change.getKey())
                } else {
                    //removal overrides any other change
                    changes[change.getKey()] = change
                }
            } else if (change.isChange) {
                if (currentChange.isAdd) {
                    //Transform the new change to an addition and replace the old one.
                    change.old = null
                    changes[change.getKey()] = change
                } else if (currentChange.isRemove) {
                    //You can't change something after it's been removed!
                    Log.w("KSyncedList", "Item removed - you can't change it now.")
                } else if (currentChange.isChange) {
                    //This change overrides the previous one.
                    changes[change.getKey()] = change
                }
            } else if (change.isAdd) {
                //You can't add something that's already added!
                Log.w("KSyncedList", "Item already added.")
            }
        }
    }

    @Deprecated("")
    private inline fun modifyChangeFile(append: Boolean = true, todo: (BufferedWriter) -> Unit) {
        try {
            changesFile.parentFile.mkdirs()
            if (!changesFile.exists()) changesFile.createNewFile()
            FileOutputStream(changesFile, append).bufferedWriter().use(todo)
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

}