package com.lightningkite.kotlincomponents.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.lightningkite.kotlincomponents.viewcontroller.ViewControllerStack
import java.util.ArrayList

/**
 */
public class ViewControllerAdapter<T>(
        public val context: Context,
        public val stack: ViewControllerStack,
        public val list: List<T>,
        private val maker: (T) -> AdaptableViewController<T>
) : BaseAdapter() {

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

}