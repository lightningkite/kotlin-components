package com.lightningkite.kotlincomponents.ui

import android.content.Context
import android.database.DataSetObserver
import android.view.ViewManager
import android.widget.LinearLayout
import android.widget.ListAdapter
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onLongClick

/**
 * Created by jivie on 8/12/15.
 */
public class AdapterLinearLayout(context: Context) : LinearLayout(context) {

    init {
        orientation = LinearLayout.VERTICAL
    }

    private var _adapter: ListAdapter? = null
    public var adapter: ListAdapter?
        get() = _adapter
        set(value) {
            _adapter = value
            _adapter?.registerDataSetObserver(observer)
            notifyDataSetChanged()
        }

    private fun notifyDataSetChanged() {
        removeAllViews()
        val _adapter = _adapter ?: return
        for (i in 0.._adapter.count - 1) {
            val view = _adapter.getView(i, null, this)
            view.onClick {
                onItemClick.invoke(this, view, i, _adapter.getItemId(i))
            }
            view.onLongClick {
                onItemLongClick.invoke(this, view, i, _adapter.getItemId(i))
            }
            addView(view)
        }
    }

    var observer: DataSetObserver = object : DataSetObserver() {
        override fun onInvalidated() {
            notifyDataSetChanged()
        }

        override fun onChanged() {
            notifyDataSetChanged()
        }
    }

    public var onItemClick: (parent: AdapterLinearLayout, view: android.view.View?, position: Int, id: Long) -> Unit = { a, b, c, d -> }
    public var onItemLongClick: (parent: AdapterLinearLayout, view: android.view.View?, position: Int, id: Long) -> Boolean = { a, b, c, d -> true }
    public fun onItemClick(func: (parent: AdapterLinearLayout, view: android.view.View?, position: Int, id: Long) -> Unit) {
        onItemClick = func
    }

    public fun onItemLongClick(func: (AdapterLinearLayout, view: android.view.View?, position: Int, id: Long) -> Boolean) {
        onItemLongClick = func
    }

    suppress("UNCHECKED_CAST")
    public fun <T> onItemClick(func: (item: T) -> Unit) {
        onItemClick { parent, view, position, id ->
            val item = adapter?.getItem(position) as T
            if (item != null) func(item)
        }
    }

    suppress("UNCHECKED_CAST")
    public fun <T> onItemLongClick(func: (item: T) -> Boolean) {
        onItemLongClick { parent, view, position, id ->
            val item = adapter?.getItem(position) as T
            if (item != null) func(item) else false
        }
    }
}

@Suppress("NOTHING_TO_INLINE") public inline fun ViewManager.adapterLinearLayout() = adapterLinearLayout {}
public inline fun ViewManager.adapterLinearLayout(init: AdapterLinearLayout.() -> Unit): AdapterLinearLayout {
    return ankoView({ AdapterLinearLayout(it) }, init)
}