package com.lightningkite.kotlincomponents.ui

import android.widget.ListView

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