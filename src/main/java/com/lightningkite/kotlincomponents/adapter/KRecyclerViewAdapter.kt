package com.lightningkite.kotlincomponents.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlincomponents.observable.KObservableList

/**
 * Created by jivie on 2/11/16.
 */
class KRecyclerViewAdapter<T : Any>(
        val list: KObservableList<T>,
        val makeView: (ItemObservable<T>) -> View
) : RecyclerView.Adapter<KRecyclerViewAdapter.ViewHolder<T>>(), MutableList<T> by list {

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T>? {
        val observable = ItemObservable<T>()
        val newView = makeView(observable)
        return ViewHolder(newView, observable)
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.observable.index = position
        holder.observable.set(list[position])
    }

    class ViewHolder<T : Any>(val itemView: View, val observable: ItemObservable<T>) : RecyclerView.ViewHolder(itemView)
}

object RecyclerViewParamAdder {
    fun <T : View> T.lparams(
            width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
            height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
            init: RecyclerView.LayoutParams.() -> Unit = {}
    ): T {
        val layoutParams = RecyclerView.LayoutParams(width, height)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }
}

inline fun <T : Any> RecyclerView.makeAdapter(list: KObservableList<T>, crossinline makeView: RecyclerViewParamAdder.(ItemObservable<T>) -> View): KRecyclerViewAdapter<T> {
    val newAdapter = KRecyclerViewAdapter(list) {
        RecyclerViewParamAdder.makeView(it)
    }
    adapter = newAdapter
    list.onAdd.add { item, position ->
        adapter.notifyItemInserted(position)
    }
    list.onRemove.add { item, position ->
        adapter.notifyItemRemoved(position)
    }
    list.onChange.add { item, position ->
        adapter.notifyItemChanged(position)
    }
    return newAdapter
}