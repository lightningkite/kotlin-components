package com.lightningkite.kotlincomponents.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import java.util.*

/**
 * A [ViewControllerAdapter] that implements [Filterable], allowing it to be used in autocomplete views.
 *
 *
 * Created by jivie on 9/2/15.
 */

@Deprecated("Use LightningAdapter instead.")
public class FilterableViewControllerAdapter<T>(
        public val activity: VCActivity,
        public val fullList: List<T>,
        public val matches: T.(String) -> Boolean,
        private val maker: (T) -> AdaptableViewController<T>
) : BaseAdapter(), Filterable {


    public companion object {
        /**
         * Creates an adapter that uses [AdaptableViewController]s for showing the data.
         *
         * @param activity The VCActivity that the view controllers should use for creating their views.
         * @param list  A list of items you want to display.
         * @param makeFunction An extension function on [AdaptableViewController] that returns the view.  [AdaptableViewController.itemBond] should be used to populate the data.
         *
         * @return An adapter with the settings specified.
         */
        public fun <T> quick(
                activity: VCActivity,
                list: List<T>,
                matches: T.(String) -> Boolean,
                makeFunction: AdaptableViewController<T>.() -> View
        ): FilterableViewControllerAdapter<T> {
            return FilterableViewControllerAdapter(activity, list, matches, fun(it: T): AdaptableViewController<T> {
                return object : AdaptableViewControllerImpl<T>(it) {
                    override fun make(activity: VCActivity): View {
                        return this.makeFunction()
                    }
                }
            })
        }
    }

    public var list: List<T> = ArrayList()

    private val viewControllers: ArrayList<AdaptableViewController<T>> = ArrayList()

    @Suppress("UNCHECKED_CAST")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val item = list[position]
        if (convertView == null) {
            val holder: AdaptableViewController<T> = maker(item)
            val view = holder.make(activity)
            viewControllers.add(holder)

            view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View) {
                    (v.tag as? AdaptableViewController<T>)?.unmake(v)
                }

                override fun onViewAttachedToWindow(v: View?) {
                }
            })

            view.tag = holder
            return view
        } else {
            //suppress UNCHECKED_CAST
            (convertView.tag as? AdaptableViewController<T>)?.item = item
        }
        (convertView.tag as? AdaptableViewController<T>)?.index = position
        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        return getView(position, convertView, parent!!)
    }

    override fun getItem(position: Int): Any? {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
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
            results.count = suggestions.size
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