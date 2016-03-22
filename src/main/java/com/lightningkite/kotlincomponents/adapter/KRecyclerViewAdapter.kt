package com.lightningkite.kotlincomponents.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlincomponents.observable.KObservable
import com.lightningkite.kotlincomponents.observable.KObservableInterface
import com.lightningkite.kotlincomponents.observable.KObservableListInterface
import com.lightningkite.kotlincomponents.observable.bind
import com.lightningkite.kotlincomponents.runAll
import java.util.*

/**
 *
 * Created by jivie on 2/11/16.
 *
 */
open class KRecyclerViewAdapter<T>(
        list: List<T>,
        val defaultValue: T,
        val makeView: (ItemObservable<T>) -> View
) : RecyclerView.Adapter<KRecyclerViewAdapter.ViewHolder<T>>(), List<T> by list {

    var list: List<T> = list
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T>? {
        val observable = ItemObservable(this)
        val newView = makeView(observable)
        val holder = ViewHolder(newView, observable)
        observable.viewHolder = holder
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) = holder.observable.update()

    class ItemObservable<T>(val parent: KRecyclerViewAdapter<T>) : ArrayList<(T) -> Unit>(), KObservableInterface<T> {
        lateinit var viewHolder:ViewHolder<T>
        val position:Int get() = viewHolder.adapterPosition

        override fun get(): T {
            if (position >= 0 && position < parent.list.size) {
                return parent.list[position]
            } else return parent.defaultValue ?: throw IllegalAccessException()
        }

        override fun set(v: T): Unit {
            if (position < 0 || position >= parent.list.size) return
            val list = parent.list as? MutableList<T> ?: throw IllegalAccessException()
            list[position] = v
            update()
        }

        override fun update() {
            runAll(get())
        }

    }

    class ViewHolder<T>(val itemView: View, val observable: ItemObservable<T>) : RecyclerView.ViewHolder(itemView)
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

inline fun <T> RecyclerView.makeAdapter(list: KObservableListInterface<T>, defaultValue: T, crossinline makeView: RecyclerViewParamAdder.(KRecyclerViewAdapter.ItemObservable<T>) -> View): KRecyclerViewAdapter<T> {
    val newAdapter = KRecyclerViewAdapter(list, defaultValue) {
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
    list.onReplace.add { list ->
        adapter.notifyDataSetChanged()
    }
    return newAdapter
}

inline fun <T> RecyclerView.makeAdapter(list: List<T>, defaultValue: T, crossinline makeView: RecyclerViewParamAdder.(KRecyclerViewAdapter.ItemObservable<T>) -> View): KRecyclerViewAdapter<T> {
    val newAdapter = KRecyclerViewAdapter(list, defaultValue) {
        RecyclerViewParamAdder.makeView(it)
    }
    adapter = newAdapter
    return newAdapter
}

inline fun <T> RecyclerView.makeAdapter(listObs: KObservable<List<T>>, defaultValue: T, crossinline makeView: RecyclerViewParamAdder.(KRecyclerViewAdapter.ItemObservable<T>) -> View): KRecyclerViewAdapter<T> {
    val newAdapter = KRecyclerViewAdapter(listObs.get(), defaultValue) {
        RecyclerViewParamAdder.makeView(it)
    }
    bind(listObs) {
        newAdapter.list = it
    }
    adapter = newAdapter
    return newAdapter
}