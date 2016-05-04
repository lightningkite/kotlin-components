package com.lightningkite.kotlincomponents.observable

import android.view.View
import com.lightningkite.kotlincomponents.collection.addSorted

/**
 * Created by jivie on 4/29/16.
 */
inline fun <E, K> View.sortedCopyOf(sourceList: KObservableListInterface<E>, copyList: KObservableList<E>, crossinline getKey: (E) -> K, crossinline sorter: (E, E) -> Boolean) {
    copyList.clear()
    for (item in sourceList) {
        copyList.addSorted(item, sorter)
    }
    listen(sourceList.onAdd) { item, index ->
        copyList.addSorted(item, sorter)
    }
    listen(sourceList.onChange) { item, index ->
        val i = copyList.indexOfFirst { getKey(it) == getKey(item) }
        copyList[i] = item
    }
    listen(sourceList.onRemove) { item, index ->
        val i = copyList.indexOfFirst { getKey(it) == getKey(item) }
        copyList.removeAt(i)
    }
    listen(sourceList.onReplace) { list ->
        copyList.clear()
        for (item in list) {
            copyList.addSorted(item, sorter)
        }
    }
}