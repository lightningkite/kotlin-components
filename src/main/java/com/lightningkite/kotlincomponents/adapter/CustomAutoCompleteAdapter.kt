package com.lightningkite.kotlincomponents.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import java.util.ArrayList

/**
 * A base adapter for quickly building an adapter with a customized look.
 * @param  The type of item to display.
 * *
 * @param  The view holder class for retaining references to view's components.
 */
@deprecated("Use AdaptableViewControllers instead.")
public class CustomAutoCompleteAdapter<ITEM, HOLDER : CustomListAdapter.ViewHolder>
(
        maker: () -> HOLDER,
        updater: (ITEM, HOLDER) -> Unit,
        stringifier: (ITEM) -> String,
        list: List<ITEM> = ArrayList()
) : BaseAdapter(), Filterable {

    public val make: () -> HOLDER = maker;
    public val update: (ITEM, HOLDER) -> Unit = updater;
    public var list: List<ITEM>? = list
    public var filteredList: List<ITEM>? = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val item = list?.get(position)
        if (convertView == null) {
            val holder: HOLDER = make()
            if (item != null) {
                update(item, holder)
            }
            holder.view.setTag(holder)
            return holder.view
        } else {
            val holder: HOLDER? = convertView.getTag() as? HOLDER
            if (item != null && holder != null) {
                update(item, holder)
            }
        }
        return convertView
    }

    override fun getItem(position: Int): Any? = filteredList?.get(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = filteredList?.size() ?: 0

    public interface ViewHolder {
        public val view: View;
    }

    private val myFilter = object : Filter() {
        private val suggestions: ArrayList<ITEM> = ArrayList()
        override fun convertResultToString(resultValue: Any?): CharSequence = stringifier(resultValue as ITEM)
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            if (list == null && constraint != null) return Filter.FilterResults()
            suggestions.clear()
            for (item in list!!) {
                if (stringifier(item).startsWith(constraint.toString())) {
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
                filteredList = results.values as ArrayList<ITEM>
                notifyDataSetChanged()
            }
        }
    }

    override fun getFilter(): Filter? = myFilter
}
