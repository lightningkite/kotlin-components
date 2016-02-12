package com.lightningkite.kotlincomponents.ui

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewManager
import android.widget.ListView
import org.jetbrains.anko.custom.ankoView

/**
 * Created by jivie on 2/9/16.
 */
fun ListView.swipeToDismiss(canRemove: (Int) -> Boolean, removeItem: (Int) -> Unit, notify: () -> Unit) {
    val listener = SwipeDismissListViewTouchListener(this,
            object : SwipeDismissListViewTouchListener.DismissCallbacks {
                override fun canDismiss(position: Int): Boolean = canRemove(position)

                override fun onDismiss(listView: ListView, reverseSortedPositions: IntArray) {
                    for (position in reverseSortedPositions) {
                        removeItem(position)
                    }
                    notify()
                }
            });
    setOnTouchListener(listener)
}

inline fun ViewManager.recyclerView() = recyclerView {}
inline fun ViewManager.recyclerView(init: RecyclerView.() -> Unit) = ankoView({ RecyclerView(it) }, init)

inline fun ViewManager.verticalRecyclerView() = verticalRecyclerView {}
inline fun ViewManager.verticalRecyclerView(init: RecyclerView.() -> Unit) = ankoView({
    RecyclerView(it).apply {
        layoutManager = LinearLayoutManager(it).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }
    }
}, init)

inline fun ViewManager.horizontalRecyclerView() = horizontalRecyclerView() {}
inline fun ViewManager.horizontalRecyclerView(init: RecyclerView.() -> Unit) = ankoView({
    RecyclerView(it).apply {
        layoutManager = LinearLayoutManager(it).apply {
            this.orientation = LinearLayoutManager.HORIZONTAL
        }
    }
}, init)