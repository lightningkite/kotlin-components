package com.lightningkite.kotlincomponents.collection

import com.github.salomonbrys.kotson.typeToken
import com.lightningkite.kotlincomponents.files.child
import com.lightningkite.kotlincomponents.files.load
import com.lightningkite.kotlincomponents.files.save
import com.lightningkite.kotlincomponents.gsonTo
import com.lightningkite.kotlincomponents.networking.NetEndpoint
import com.lightningkite.kotlincomponents.networking.NetMethod
import com.lightningkite.kotlincomponents.networking.Networking
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
inline fun <reified T : Any, reified K : Any> KSyncedList(folder: File, noinline getKey: (T) -> K): KSyncedList<T, K> = KSyncedList(folder, typeToken<T>(), getKey)

open class KSyncedList<T : Any, K : Any>(
        folder: File,
        val type: Type,
        val getKey: (T) -> K,
        val innerList: KObservableListInterface<T> = KObservableList()
) : KObservableListInterface<T> by innerList {

    init {
        folder.mkdirs()
    }

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
    val file: File get() = folder.child("data.json")
    val changesFile: File get() = folder.child("changes.json")

    val changeType: ParameterizedType = object : ParameterizedType {
        override fun getRawType(): Type? = ItemChange::class.java
        override fun getOwnerType(): Type? = null
        override fun getActualTypeArguments(): Array<out Type>? = arrayOf(type)
    }

    val listType: ParameterizedType = object : ParameterizedType {
        override fun getRawType(): Type? = ArrayList::class.java
        override fun getOwnerType(): Type? = null
        override fun getActualTypeArguments(): Array<out Type>? = arrayOf(type)
    }

    fun syncREST(
            endpoint: NetEndpoint,
            syncPull: () -> List<T> = {
                Networking.stream(endpoint.request(NetMethod.GET)).gson<ArrayList<T>>(listType) ?: throw IllegalArgumentException("Could not parse data")
            },
            merge: ((T, T) -> T)? = null
    ): List<ItemChange<T>> {
        val failed = processChanges {
            var success = true
            if (it.isAdd) {
                endpoint.syncPost<Unit>(it.new) { success = false; true }
            } else if (it.isChange) {
                endpoint.sub(getKey(it.old!!).toString()).syncPut<Unit>(it.new) { success = false; true }
            } else if (it.isRemove) {
                endpoint.sub(getKey(it.old!!).toString()).syncDelete<Unit>(null) { success = false; true }
            } else if (it.isClear) {
                endpoint.syncDelete<Unit>(null) { success = false; true }
            }
            success
        }

        val newData = syncPull()
        updateFromServer(newData, merge, false)
        return failed
    }

    fun load() {
        try {
            load(file, type)
            loadChanges()
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveClearingChanges() {
        try {
            save(file)
            modifyChangeFile(false) {}
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Does something on all of the changes, returning any that failed.  The successful changes are deleted.
     **/
    fun processChanges(forChange: (ItemChange<T>) -> Boolean): List<ItemChange<T>> {
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
        return failedChanges
    }

    fun updateFromServer(list: List<T>, merge: ((T, T) -> T)? = null, clearChanges: Boolean = false) {
        if (merge == null) {
            innerList.clear()
            innerList.addAll(list)
        } else {
            var toDelete = HashSet<K>(innerList.map { getKey(it) })
            for (new in list) {
                toDelete.remove(getKey(new))
                val index = innerList.indexOfFirst { getKey(it) == getKey(new) }
                if (index == -1) {
                    //new item from server
                    innerList.add(new)
                } else {
                    //updated item from server
                    innerList[index] = merge(innerList[index], new)
                }
            }
            innerList.removeAll { toDelete.contains(getKey(it)) }
        }

        if (clearChanges) {
            modifyChangeFile(false) {}
        }
    }

    private fun loadChanges() {
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
                    val index = indexOfFirst { getKey(change.old!!) == getKey(it) }
                    if (index != -1) {
                        innerList.removeAt(index)
                    }
                } else {
                    //change
                    val index = indexOfFirst { getKey(change.old!!) == getKey(it) }
                    if (index != -1) {
                        innerList[index] = change.new!!
                    }
                }
            }
        }
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
            FileOutputStream(changesFile, append).bufferedWriter().use(todo)
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

}