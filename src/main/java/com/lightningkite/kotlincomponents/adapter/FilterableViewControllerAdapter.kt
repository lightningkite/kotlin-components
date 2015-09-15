package com.lightningkite.kotlincomponents.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import com.lightningkite.kotlincomponents.viewcontroller.ViewControllerStack
import java.util.ArrayList

/**
 * Created by jivie on 9/2/15.
 */

public class FilterableViewControllerAdapter<T>(
        public val context: Context,
        public val stack: ViewControllerStack,
        public val fullList: List<T>,
        public val matches: T.(String) -> Boolean,
        private val maker: (T) -> AdaptableViewController<T>
) : BaseAdapter(), Filterable {

    public var list: List<T> = ArrayList()

    private val viewControllers: ArrayList<AdaptableViewController<T>> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val item = list.get(position)
        if (convertView == null) {
            val holder: AdaptableViewController<T> = maker(item)
            val view = holder.make(context, stack)
            viewControllers.add(holder)

            view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View) {
                    (v.getTag() as? AdaptableViewController<T>)?.dispose(v)
                }

                override fun onViewAttachedToWindow(v: View?) {
                }
            })

            view.setTag(holder)
            return view
        } else {
            (convertView.getTag() as? AdaptableViewController<T>)?.item = item
        }
        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        return getView(position, convertView, parent!!)
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

    public fun update() {
        for (vc in viewControllers) {
            vc.itemBond.update()
        }
    }

    private val myFilter = object : Filter() {
        private val suggestions: ArrayList<T> = ArrayList()
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            if (constraint == null) return Filter.FilterResults()
            suggestions.clear()
            val constraintString = constraint.toString()
            for (item in fullList) {
                if (item.matches(constraintString)) {
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
                list = results.values as ArrayList<T>
                notifyDataSetChanged()
            }
        }
    }

    override fun getFilter(): Filter? = myFilter
}