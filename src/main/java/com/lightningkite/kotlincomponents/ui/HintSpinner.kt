package com.lightningkite.kotlincomponents.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lightningkite.kotlincomponents.run
import org.jetbrains.anko.text

/**
 * Created by jivie on 8/25/15.
 */
public class HintSpinner(context: Context, mode: Int, val hint: View = TextView(context).run { text = "Select one" }) : Spinner(context, mode) {

    private var wrapper: AdapterWrapper? = null

    override fun setAdapter(adapter: SpinnerAdapter?) {
        if (adapter == null) super.setAdapter(adapter)
        else {
            wrapper = AdapterWrapper(adapter)
            super.setAdapter(wrapper)
        }
    }

    override fun getAdapter(): SpinnerAdapter? {
        return super.getAdapter()
    }

    override fun setOnItemSelectedListener(listener: AdapterView.OnItemSelectedListener?) {
        if (listener == null) {
            super.setOnItemClickListener(null)
            return
        }
        super.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                listener.onNothingSelected(parent)
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newPosition = if (position == getCount()) -1 else position
                listener.onItemSelected(parent, view, newPosition, id)
            }
        })
    }

    override fun setSelection(position: Int) {
        if (position == -1) super.setSelection(getCount())
        else super.setSelection(position)
    }

    private inner class AdapterWrapper(val innerAdapter: SpinnerAdapter) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            if (position == getCount()) {
                return hint
            } else {
                val oldView = if (convertView != hint) convertView else null
                return innerAdapter.getView(position, oldView, parent)
            }
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            return getView(position, convertView, parent!!)
        }

        override fun getItem(position: Int): Any? {
            if (position < getCount()) return innerAdapter.getItem(position)
            else return null
        }

        override fun getItemId(position: Int): Long {
            if (position < getCount()) return innerAdapter.getItemId(position)
            else return -1
        }

        override fun getCount(): Int {
            return innerAdapter.getCount()
        }
    }
}

public fun ViewGroup.hintSpinner(mode: Int, hint: View, setup: HintSpinner.() -> Unit): HintSpinner {
    val layout = HintSpinner(getContext(), mode, hint)
    layout.setup();
    addView(layout)
    return layout
}

public var HintSpinner.adapter: SpinnerAdapter?
    get() = this.getAdapter()
    set(value) = this.setAdapter(value)