package com.lightningkite.kotlincomponents.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import java.util.*

/**
 * Created by jivie on 8/14/15.
 */

@Deprecated("Use AdaptableViewControllers instead.")
public interface DataDisplayer<ITEM> {
    public fun create():View
    public fun update(item: ITEM)
    public fun create(item: ITEM): View {
        val v = create()
        update(item)
        return v
    }
}

@Deprecated("Use AdaptableViewControllers instead.")
public open class DataDisplayerAdapter<ITEM>(var list: List<ITEM>, val make: DataDisplayerAdapter<ITEM>.() -> DataDisplayer<ITEM>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val item = list.get(position)
        if (convertView == null) {
            val holder: DataDisplayer<ITEM> = make()
            val view = holder.create()
            holder.update(item)
            view.setTag(holder)
            return view
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

@deprecated("Use AdaptableViewControllers instead.")
public class DataDisplayerFilterableAdapter<ITEM>(
        var fullList: List<ITEM>,
        val predicate: (ITEM, CharSequence) -> Boolean,
        make: DataDisplayerAdapter<ITEM>.() -> DataDisplayer<ITEM>,
        stringifier: (ITEM) -> String
) : DataDisplayerAdapter<ITEM>(ArrayList(), make), Filterable {

    public constructor(
            fullList: List<ITEM>,
            stringifier: (ITEM) -> String,
            make: DataDisplayerAdapter<ITEM>.() -> DataDisplayer<ITEM>
    ) : this(fullList, { item, search -> stringifier(item).startsWith(search.toString(), true) }, make, stringifier)


    private val myFilter = object : Filter() {
        private val suggestions: ArrayList<ITEM> = ArrayList()
        override fun convertResultToString(resultValue: Any?): CharSequence = stringifier(resultValue as ITEM)
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            if (constraint == null) return Filter.FilterResults()
            suggestions.clear()
            for (item in fullList) {
                if (predicate(item, constraint)) {
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