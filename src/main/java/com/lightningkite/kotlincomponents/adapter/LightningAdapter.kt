package com.lightningkite.kotlincomponents.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.lightningkite.kotlincomponents.observable.KObservable
import com.lightningkite.kotlincomponents.observable.KObservableInterface
import com.lightningkite.kotlincomponents.observable.bind

/**
 * Created by josep on 1/24/2016.
 */
class LightningAdapter<T>(list: List<T>, val makeView: (ItemObservable<T>) -> View) : BaseAdapter() {

    class ItemObservable<T>(init:T) : KObservable<T>(init){
        var index:Int = 0
        override fun remove(element: (T) -> Unit): Boolean {
            return super.remove(element)
        }
    }

    var list:List<T> = list
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun getCount(): Int = list.size
    override fun getItem(position: Int): Any? = list[position]
    override fun getItemId(position: Int): Long = position.toLong()

    @Suppress("UNCHECKED_CAST")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val item = list[position]
        if (convertView == null) {
            val newObs = ItemObservable(list[position])
            val newView = makeView(newObs)
            newView.tag = newObs
            newObs.index = position
            return newView
        } else {
            val obs = convertView.tag as ItemObservable<T>
            obs.index = position
            obs.set(list[position])
        }
        return convertView
    }

}

fun <T, C : List<T>> View.lightningAdapter(listObs: KObservableInterface<C>, makeView: (LightningAdapter.ItemObservable<T>) -> View): LightningAdapter<T> {
    val result = LightningAdapter(listObs.get(), makeView)
    bind(listObs) {
        result.list = it
    }
    return result
}