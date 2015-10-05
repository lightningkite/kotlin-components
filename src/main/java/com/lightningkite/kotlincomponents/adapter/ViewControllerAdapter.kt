package com.lightningkite.kotlincomponents.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.lightningkite.kotlincomponents.viewcontroller.ViewControllerStack
import java.util.*

/**
 */
public class ViewControllerAdapter<T>(
        public val context: Context,
        public val stack: ViewControllerStack,
        public val list: List<T>,
        private val maker: (T) -> AdaptableViewController<T>
) : BaseAdapter() {

    public companion object {
        public fun <T> quick(
                context: Context,
                stack: ViewControllerStack,
                list: List<T>,
                makeFunction: AdaptableViewController<T>.() -> View
        ): ViewControllerAdapter<T> {
            return ViewControllerAdapter(context, stack, list, fun(it: T): AdaptableViewController<T> {
                return object : AdaptableViewControllerImpl<T>(it) {
                    override fun make(context: Context, stack: ViewControllerStack): View {
                        return this.makeFunction()
                    }
                }
            })
        }
    }

    private val viewControllers: ArrayList<AdaptableViewController<T>> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val item = list.get(position)
        if (convertView == null) {
            val holder: AdaptableViewController<T> = maker(item)
            val view = holder.make(context, stack)
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
            (convertView.tag as? AdaptableViewController<T>)?.item = item
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