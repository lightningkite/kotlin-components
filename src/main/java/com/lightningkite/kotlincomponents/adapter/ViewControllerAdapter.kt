package com.lightningkite.kotlincomponents.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import java.util.*

/**
 * Creates an adapter that uses [ViewController]s for showing the data.
 * This constructor will typically not be used, as it is more verbose.  Take a look at [ViewControllerAdapter.quick] instead.
 *
 * @param activity The VCActivity that the view controllers should use for creating their views.
 * @param list  A list of items you want to display.
 * @param maker A function that makes an [AdaptableViewController] that takes something of type T.
 * @return
 */
public class ViewControllerAdapter<T>(
        public val activity: VCActivity,
        list: List<T>,
        private val maker: (T) -> AdaptableViewController<T>
) : BaseAdapter() {

    /**
     * The list the adapter uses.  You can safely change this, but note that if you do not change
     * instance but change the instance itself that you are responsible for calling
     * [android.widget.BaseAdapter.notifyDataSetChanged].
     */
    public var list: List<T> = list
        get() {
            return field
        }
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    public companion object {
        /**
         * Creates an adapter that uses [com.lightningkite.kotlincomponents.viewcontroller.ViewController]s for showing the data.
         *
         * @param activity The VCActivity that the view controllers should use for creating their views.
         * @param list  A list of items you want to display.
         * @param makeFunction An extension function on [AdaptableViewController] that returns the view.  [AdaptableViewController.itemBond] should be used to populate the data.  For more information, see [com.lightningkite.kotlincomponents.databinding.Bond].
         *
         * @return An adapter with the settings specified.
         */
        public fun <T> quick(
                activity: VCActivity,
                list: List<T>,
                makeFunction: AdaptableViewController<T>.() -> View
        ): ViewControllerAdapter<T> {
            return ViewControllerAdapter(activity, list, fun(it: T): AdaptableViewController<T> {
                return object : AdaptableViewControllerImpl<T>(it) {
                    override fun make(activity: VCActivity): View {
                        return this.makeFunction()
                    }
                }
            })
        }
    }

    /**
     * Calls update on all of the [AdaptableViewController.itemBond]s.
     */
    public fun update() {
        for (vc in viewControllers) {
            vc.itemBond.update()
        }
    }

    private val viewControllers: ArrayList<AdaptableViewController<T>> = ArrayList()

    @Suppress("UNCHECKED_CAST")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val item = list.get(position)
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
        return list.size
    }

}