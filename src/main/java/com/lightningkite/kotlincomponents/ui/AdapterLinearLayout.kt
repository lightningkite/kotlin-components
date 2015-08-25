package com.lightningkite.kotlincomponents.ui

import android.content.Context
import android.database.DataSetObserver
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListAdapter

/**
 * Created by jivie on 8/12/15.
 */
public class AdapterLinearLayout(context: Context) : LinearLayout(context) {
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
        if (_adapter == null) return
        for (i in 0.._adapter!!.getCount() - 1) {
            addView(_adapter!!.getView(i, null, this))
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
}

public fun ViewGroup.adapterLinearLayout(setup: AdapterLinearLayout.() -> Unit): AdapterLinearLayout {
    val layout = AdapterLinearLayout(getContext())
    layout.setup();
    addView(layout)
    return layout
}