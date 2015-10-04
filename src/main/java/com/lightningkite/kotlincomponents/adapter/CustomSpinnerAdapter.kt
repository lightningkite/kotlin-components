package com.lightningkite.kotlincomponents.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import java.util.*


/**
 * A base adapter for quickly building an adapter with a customized look.
 * @param  The type of item to display.
 * *
 * @param  The view holder class for retaining references to view's components.
 */
@Deprecated("Use AdaptableViewControllers instead.")
public class CustomSpinnerAdapter<ITEM, HOLDER : CustomListAdapter.ViewHolder>
(
        maker: () -> HOLDER,
        updater: (ITEM, HOLDER) -> Unit,
        list: List<ITEM> = ArrayList(),
        hint: () -> View = { throw UnsupportedOperationException() }
) : BaseAdapter(), SpinnerAdapter {

    public val make: () -> HOLDER = maker;
    public val update: (ITEM, HOLDER) -> Unit = updater;
    public var list: List<ITEM>? = list
    public val hint: () -> View = hint

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        if (position == -1) {
            return hint()
        }
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

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        return getView(position, convertView, parent!!)
    }

    override fun getItem(position: Int): Any? = list?.get(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list?.size() ?: 0

    public interface ViewHolder {
        public val view: View;
    }
}
