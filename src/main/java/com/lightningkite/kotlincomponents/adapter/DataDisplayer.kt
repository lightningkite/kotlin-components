package com.lightningkite.kotlincomponents.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import java.util.ArrayList

/**
 * Created by jivie on 8/14/15.
 */

public interface DataDisplayer<ITEM> {
    public val view: View
    public fun update(item: ITEM)
}

public open class DataDisplayerAdapter<ITEM>(var list: List<ITEM>, val make: () -> DataDisplayer<ITEM>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val item = list.get(position)
        if (convertView == null) {
            val holder: DataDisplayer<ITEM> = make()
            holder.update(item)
            holder.view.setTag(holder)
            return holder.view
        } else {
            (convertView.getTag() as? DataDisplayer<ITEM>)?.update(item)
        }
        return convertView
    }

    public fun update(list: List<ITEM>) {
        this.list = list
        notifyDataSetChanged()
    }

    public fun getItemTyped(position: Int): ITEM {
        return list.get(position)
    }

    override fun getItem(position: Int): Any? {
        return list.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        return getView(position, convertView, parent!!)
    }
}

public class DataDisplayerFilterableAdapter<ITEM>(
        var fullList: List<ITEM>,
        val predicate: (ITEM, CharSequence) -> Boolean,
        make: () -> DataDisplayer<ITEM>
) : DataDisplayerAdapter<ITEM>(ArrayList(), make), Filterable {


    private val myFilter = object : Filter() {
        private val suggestions: ArrayList<ITEM> = ArrayList()
        override fun convertResultToString(resultValue: Any?): CharSequence = stringifier(resultValue as ITEM)
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            if (constraint != null) return Filter.FilterResults()
            suggestions.clear()
            for (item in list) {
                if (predicate(item, constraint!!)) {
                    suggestions.add(item)
                }
            }
            val results = Filter.FilterResults()
            results.count = suggestions.size()
            results.values = suggestions
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults) {
            if (results.count > 0) {
                list = results.values as ArrayList<ITEM>
                notifyDataSetChanged()
            }
        }
    }

    override fun getFilter(): Filter? = myFilter
}