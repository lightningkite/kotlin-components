package com.lightningkite.kotlincomponents.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import java.util.*

/**
 * A base adapter for quickly building an adapter with a customized look.
 * @param  The type of item to display.
 * *
 * @param  The view holder class for retaining references to view's components.
 */
@Deprecated("Use AdaptableViewControllers instead.")
public class CustomListAdapter<ITEM, HOLDER : CustomListAdapter.ViewHolder>
(
        maker: () -> HOLDER,
        updater: (ITEM, HOLDER) -> Unit,
        list: List<ITEM> = ArrayList()
) : BaseAdapter() {

    public val make: () -> HOLDER = maker;
    public val update: (ITEM, HOLDER) -> Unit = updater;
    public var list: List<ITEM>? = list

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val item = list?.get(position)
        if (convertView == null) {
            val holder: HOLDER = make()
            if (item != null) {
                update(item, holder)
            }
            holder.view.tag = holder
            return holder.view
        } else {
            val holder: HOLDER? = convertView.tag as? HOLDER
            if (item != null && holder != null) {
                update(item, holder)
            }
        }
        return convertView
    }

    override fun getItem(position: Int): Any? = list?.get(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list?.size() ?: 0

    public interface ViewHolder {
        public val view: View;
    }
}
