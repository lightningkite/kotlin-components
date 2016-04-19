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
                        result = PullResult(SyncError("Could not parse data.", null, response))
                    } else {
                        result = PullResult(list)
                    }
                } catch(e: Exception) {
                    IllegalArgumentException("Could not parse data: $response", e).printStackTrace()
                    result = PullResult(SyncError("Could not parse data.", null, response))
                }
                result
            },
            syncPush = { change ->
                val endpoint = getEndpoint()
                var error: SyncError? = null
                if (change.isAdd) {
                    Log.i("KSyncedList", "Posting new item: ${change.new!!.gsonTo()}")
                    endpoint.syncPost<Unit>(change.new) { error = SyncError(it.string(), change, it); true }
                } else if (change.isChange) {
                    Log.i("KSyncedList", "Putting change: ${change.new!!.gsonTo()}")
                    endpoint.sub(change.old!!.getKey().toString()).syncPatch<Unit>(change.new) { error = SyncError(it.string(), change, it); true }
                } else if (change.isRemove) {
                    Log.i("KSyncedList", "Deleting item: ${change.old!!.gsonTo()}")
                    endpoint.sub(change.old!!.getKey().toString()).syncDelete<Unit>(null) { error = SyncError(it.string(), change, it); true }
                } else if (change.isClear) {
                    Log.i("KSyncedList", "Clearing items.")
                    endpoint.syncDelete<Unit>(null) { error = SyncError(it.string(), change, it); true }
                }
                error
            }
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
            syncPush = { change ->
                val endpoint = getEndpoint()
                var error: SyncError? = null
                if (change.isAdd) {
                    Log.i("KSyncedList", "Posting new item: ${change.new!!.gsonTo()}")
                    endpoint.syncPost<Unit>(change.new) { error = SyncError(it.string(), change, it); true }
                } else if (change.isChange) {
                    Log.i("KSyncedList", "Putting change: ${change.new!!.gsonTo()}")
                    endpoint.sub(change.old!!.getKey().toString()).syncPatch<Unit>(change.new) { error = SyncError(it.string(), change, it); true }
                } else if (change.isRemove) {
                    Log.i("KSyncedList", "Deleting item: ${change.old!!.gsonTo()}")
                    endpoint.sub(change.old!!.getKey().toString()).syncDelete<Unit>(null) { error = SyncError(it.string(), change, it); true }
                } else if (change.isClear) {
                    Log.i("KSyncedList", "Clearing items.")
                    endpoint.syncDelete<Unit>(null) { error = SyncError(it.string(), change, it); true }
                }
                error
            }
    )
}

open class KSyncedList<T : KSyncedListItem<T, K>, K : Any>(
        val type: Type,
        val getFolder: () -> File,
        val syncPull: () -> PullResult<T>,
        val syncPush: (ItemChange<T, K>) -> SyncError?,
        val innerList: KObservableListInterface<T> = KObservableList()
) : KObservableListInterface<T> by innerList, Syncable {

    init {
        innerList.onAdd.add { item, index ->
            item.parent = this
        }
    }

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
                    try {
                        loadChanges()
                    } catch(e: Exception) {
                        e.printStackTrace()
                    }
                    this@KSyncedList.withReduceAsync(
                            {
                                Log.i("KSyncedList", "Syncing child ${getKey()}...")
                                sync(it)
                            },
                            ArrayList<SyncError>(failed),
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
            loadChanges()
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    override fun saveLocal() {
        //We don't have to do anything for this, because it saves changes as it goes.
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
    private fun processChanges(forChange: (ItemChange<T, K>) -> SyncError?): List<SyncError> {
        if (!changesFile.exists()) return ArrayList()
        val changes: ArrayList<ItemChange<T, K>> = ArrayList()
        try {
            changes.load(changesFile, changeType)
        } catch(e: Exception) {
            e.printStackTrace()
        }
        val failedChanges = ArrayList<ItemChange<T, K>>()
        for (change in changes) {
            change.belongsTo = this
            change.error?.change = change
            val error = forChange(change)
            if (error != null) {
                change.error = error
                failedChanges.add(change)
            }
        }
        modifyChangeFile(false) {
            numChanges = failedChanges.size
            for (change in failedChanges) {
                it.appendln(change.gsonTo())
            }
        }
        return failedChanges.map { it.error!! }
    }

    private fun loadChanges() {
        if (!changesFile.exists()) return
        val changes: ArrayList<ItemChange<T, K>> = ArrayList()
        changes.load(changesFile, changeType)
        for (change in changes) {
            println(change)
            change.belongsTo = this
            change.error?.change = change
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
                    } else {
                        //remove the useless deletion
                        Log.i("KSyncedList", "Removing useless deletion.")
                        changes.remove(change)
                    }
                } else {
                    //change
                    val index = indexOfFirst { change.old!!.getKey() == it.getKey() }
                    if (index != -1) {
                        innerList[index] = change.new!!
                    } else {
                        //remove the useless change
                        Log.i("KSyncedList", "Removing useless change.")
                        changes.remove(change)
                    }
                }
            }
        }
        //write changes
        changes.save(changesFile)
    }

    fun update(item: T) {
        Log.i("KSyncedList", "Updating item ${item.getKey()}")
        val index = this.indexOfFirst { item.getKey() == it.getKey() }
        this[index].merge(item)
        modifyChangeFile {
            numChanges++
            it.appendln(ItemChange(item, item).gsonTo())
        }
    }

    fun update(index: Int) {
        val item = this[index]
        Log.i("KSyncedList", "Updating item ${item.getKey()}")
        modifyChangeFile {
            numChanges++
            it.appendln(ItemChange(item, item).gsonTo())
        }
    }

    override fun add(element: T): Boolean {
        Log.i("KSyncedList", "Adding item ${element.getKey()}")
        element.parent = this
        modifyChangeFile {
            numChanges++
            it.appendln(ItemChange(null, element).gsonTo())
        }
        return innerList.add(element)
    }

    override fun add(index: Int, element: T) {
        Log.i("KSyncedList", "Adding item ${element.getKey()}")
        element.parent = this
        modifyChangeFile {
            numChanges++
            it.appendln(ItemChange(null, element).gsonTo())
        }
        innerList.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        modifyChangeFile {
            for (element in elements) {
                Log.i("KSyncedList", "Adding item ${element.getKey()}")
                element.parent = this
                numChanges++
                it.appendln(ItemChange(null, element).gsonTo())
            }
        }
        return innerList.addAll(index, elements)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        modifyChangeFile {
            for (element in elements) {
                Log.i("KSyncedList", "Adding item ${element.getKey()}")
                element.parent = this
                numChanges++
                it.appendln(ItemChange(null, element).gsonTo())
            }
        }
        return innerList.addAll(elements)
    }

    override fun remove(element: T): Boolean {
        modifyChangeFile {
            Log.i("KSyncedList", "Removing item ${element.getKey()}")
            numChanges++
            element.parent = null
            it.appendln(ItemChange(element, null).gsonTo())
        }
        return innerList.remove(element)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        modifyChangeFile {
            for (element in elements) {
                Log.i("KSyncedList", "Removing item ${element.getKey()}")
                numChanges++
                element.parent = null
                it.appendln(ItemChange(element, null).gsonTo())
            }
        }
        return innerList.removeAll(elements)
    }

    override fun removeAt(index: Int): T {
        modifyChangeFile {
            Log.i("KSyncedList", "Removing item ${this[index].getKey()}")
            numChanges++
            this[index].parent = null
            it.appendln(ItemChange(this[index], null).gsonTo())
        }
        return innerList.removeAt(index)
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun set(index: Int, element: T): T {
        val previous = this[index]
        modifyChangeFile {
            Log.i("KSyncedList", "Updating item ${element.getKey()}")
            element.parent = this
            numChanges++
            it.appendln(ItemChange(previous, element).gsonTo())
        }
        return innerList.set(index, element)
    }

    override fun clear() {
        modifyChangeFile(false) {
            it.appendln(ItemChange<T, K>(null, null).gsonTo())
            numChanges = 1
        }
        innerList.clear()
    }

    override fun replace(list: List<T>) {
        clear()
        addAll(list)
    }

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