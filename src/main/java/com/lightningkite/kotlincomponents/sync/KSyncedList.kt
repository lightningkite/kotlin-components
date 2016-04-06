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
import com.lightningkite.kotlincomponents.observable.KObservableBuffered
import com.lightningkite.kotlincomponents.observable.KObservableInterface
import com.lightningkite.kotlincomponents.observable.KObservableList
import com.lightningkite.kotlincomponents.observable.KObservableListInterface
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * Created by jivie on 3/29/16.
 */
inline fun <reified T : Mergeable<K, T>, reified K : Any> KSyncedList(
        folder: File,
        endpoint: NetEndpoint
): KSyncedList<T, K> {
    val listType: ParameterizedType = object : ParameterizedType {
        override fun getRawType(): Type? = ArrayList::class.java
        override fun getOwnerType(): Type? = null
        override fun getActualTypeArguments(): Array<out Type>? = arrayOf(typeToken<T>())
    }
    return KSyncedList(
            typeToken<T>(),
            folder,
            {
                val response = Networking.sync(endpoint.request(NetMethod.GET))
                try {
                    response.gson<ArrayList<T>>(listType) ?: throw IllegalArgumentException("Could not parse data: $$response")
                } catch(e: Exception) {
                    throw IllegalArgumentException("Could not parse data: $response", e)
                }
            },
            {
                var success = true
                if (it.isAdd) {
                    endpoint.syncPost<Unit>(it.new) { success = false; true }
                    Log.i("KSyncedList", "Posting new item: ${it.new}")
                } else if (it.isChange) {
                    endpoint.sub(it.old!!.getKey().toString()).syncPut<Unit>(it.new) { success = false; true }
                    Log.i("KSyncedList", "Putting change: ${it.new}")
                } else if (it.isRemove) {
                    endpoint.sub(it.old!!.getKey().toString()).syncDelete<Unit>(null) { success = false; true }
                    Log.i("KSyncedList", "Deleting item: ${it.old}")
                } else if (it.isClear) {
                    endpoint.syncDelete<Unit>(null) { success = false; true }
                    Log.i("KSyncedList", "Clearing items.")
                }
                success
            }
    )
}

inline fun <reified T : Mergeable<K, T>, reified K : Any> KSyncedList(
        folder: File,
        endpoint: NetEndpoint,
        noinline customPull: () -> List<T>?
): KSyncedList<T, K> {
    val listType: ParameterizedType = object : ParameterizedType {
        override fun getRawType(): Type? = ArrayList::class.java
        override fun getOwnerType(): Type? = null
        override fun getActualTypeArguments(): Array<out Type>? = arrayOf(typeToken<T>())
    }
    return KSyncedList(
            typeToken<T>(),
            folder,
            customPull,
            {
                var success = true
                if (it.isAdd) {
                    endpoint.syncPost<Unit>(it.new) { success = false; true }
                    Log.i("KSyncedList", "Posting new item: ${it.new}")
                } else if (it.isChange) {
                    endpoint.sub(it.old!!.getKey().toString()).syncPut<Unit>(it.new) { success = false; true }
                    Log.i("KSyncedList", "Putting change: ${it.new}")
                } else if (it.isRemove) {
                    endpoint.sub(it.old!!.getKey().toString()).syncDelete<Unit>(null) { success = false; true }
                    Log.i("KSyncedList", "Deleting item: ${it.old}")
                } else if (it.isClear) {
                    endpoint.syncDelete<Unit>(null) { success = false; true }
                    Log.i("KSyncedList", "Clearing items.")
                }
                success
            }
    )
}

open class KSyncedList<T : Mergeable<K, T>, K : Any>(
        val type: Type,
        folder: File,
        val syncPull: () -> List<T>?,
        val syncPush: (ItemChange<T>) -> Boolean,
        val innerList: KObservableListInterface<T> = KObservableList()
) : KObservableListInterface<T> by innerList, Syncable {

    var folder: File = folder
        set(newFolder) {
            //move backup and changes file
            if (folder == newFolder) return
            if (file.exists()) {
                file.renameTo(newFolder.child("data.json"))
            }
            if (changesFile.exists()) {
                changesFile.renameTo(newFolder.child("changes.json"))
            }
            field = newFolder
        }
    private val file: File get() = folder.child("data.json")
    private val changesFile: File get() = folder.child("changes.json")

    private val changeType: ParameterizedType = object : ParameterizedType {
        override fun getRawType(): Type? = ItemChange::class.java
        override fun getOwnerType(): Type? = null
        override fun getActualTypeArguments(): Array<out Type>? = arrayOf(type)
    }

    fun observable(key: K): KObservableInterface<T> {
        return object : KObservableBuffered<T>() {

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

            override fun getter(): T {
                if (isEmpty()) index = this@KSyncedList.indexOfFirst { it.getKey() == key }
                return this@KSyncedList[index]
            }

            override fun setter(value: T) {
                this@KSyncedList[index] = value
            }

            override fun update() {
                //This may look redundant, but it's not.  This is very important.  It fires the
                //write command on this list, writing the update to the local cache, ensuring it
                //will make it back to the server eventually.
                this@KSyncedList[index] = this@KSyncedList[index]
                super.update()
            }
        }
    }

    override fun sync(onComplete: () -> Unit) {
        //online sync
        doAsync {
            processChanges(syncPush)

            Log.i("KSyncedList", "Pulling new data...")
            val newData = syncPull()
            if (newData != null) {
                doUiThread {
                    update(newData)
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
                    onComplete()
                }
            } else {
                Log.e("KSyncedList", "Failed to pull new data.")
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

    fun update(list: List<T>) {
        innerList.merge(list)
    }

    fun update(item: T) {
        val index = this.indexOfFirst { item.getKey() == it.getKey() }
        this[index].merge(item)
        modifyChangeFile {
            it.appendln(ItemChange(item, item).gsonTo())
        }
    }

    fun update(index: Int) {
        val item = this[index]
        modifyChangeFile {
            it.appendln(ItemChange(item, item).gsonTo())
        }
    }

    /**
     * Does something on all of the changes.  The successful changes are deleted.
     **/
    private fun processChanges(forChange: (ItemChange<T>) -> Boolean) {
        if (!changesFile.exists()) return
        val changes: ArrayList<ItemChange<T>> = ArrayList()
        try {
            changes.load(changesFile, changeType)
        } catch(e: Exception) {
            e.printStackTrace()
        }
        val failedChanges = ArrayList<ItemChange<T>>()
        for (change in changes) {
            if (!forChange(change)) {
                failedChanges.add(change)
            }
        }
        modifyChangeFile(false) {
            for (change in failedChanges) {
                it.appendln(change.gsonTo())
            }
        }
    }

    private fun loadChanges() {
        if (!changesFile.exists()) return
        val changes: ArrayList<ItemChange<T>> = ArrayList()
        changes.load(changesFile, changeType)
        for (change in changes) {
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

    override fun add(element: T): Boolean {
        modifyChangeFile {
            it.appendln(ItemChange(null, element).gsonTo())
        }
        return innerList.add(element)
    }

    override fun add(index: Int, element: T) {
        modifyChangeFile {
            it.appendln(ItemChange(null, element).gsonTo())
        }
        innerList.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        modifyChangeFile {
            for (element in elements) {
                it.appendln(ItemChange(null, element).gsonTo())
            }
        }
        return innerList.addAll(index, elements)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        modifyChangeFile {
            for (element in elements) {
                it.appendln(ItemChange(null, element).gsonTo())
            }
        }
        return innerList.addAll(elements)
    }

    override fun remove(element: T): Boolean {
        modifyChangeFile {
            it.appendln(ItemChange(element, null).gsonTo())
        }
        return innerList.remove(element)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        modifyChangeFile {
            for (element in elements) {
                it.appendln(ItemChange(element, null).gsonTo())
            }
        }
        return innerList.removeAll(elements)
    }

    override fun removeAt(index: Int): T {
        modifyChangeFile {
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
            it.appendln(ItemChange(previous, element).gsonTo())
        }
        return innerList.set(index, element)
    }

    override fun clear() {
        modifyChangeFile(false) {
            it.appendln(ItemChange(null, null).gsonTo())
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