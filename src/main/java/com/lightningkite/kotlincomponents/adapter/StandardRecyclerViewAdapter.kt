package com.lightningkite.kotlincomponents.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlincomponents.networking.PagedEndpoint
import com.lightningkite.kotlincomponents.observable.KObservable
import com.lightningkite.kotlincomponents.observable.KObservableInterface
import com.lightningkite.kotlincomponents.observable.KObservableListInterface
import com.lightningkite.kotlincomponents.observable.bind
import com.lightningkite.kotlincomponents.runAll
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoContextImpl
import java.util.*

/**
 * Created by jivie on 5/4/16.
 */
class StandardRecyclerViewAdapter<T>(
        val context: Context,
        list: List<T>,
        val makeView: AnkoContext<StandardRecyclerViewAdapter<T>>.(ItemObservable<T>) -> Unit
) : RecyclerView.Adapter<StandardRecyclerViewAdapter.ViewHolder<T>>(), List<T> by list {

    var list: List<T> = list
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onScrollToBottom: (() -> Unit)? = null

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T>? {
        val observable = ItemObservable(this)
        itemObservables.add(observable)
        val newView = SRVAContext(this, context).apply { makeView(observable) }.view
        val holder = ViewHolder(newView, observable)
        observable.viewHolder = holder
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        if (itemCount > 0 && position + 1 == itemCount) {
            onScrollToBottom?.invoke()
        }
        holder.observable.update()
    }


    val itemObservables = ArrayList<ItemObservable<T>>()

    class ItemObservable<T>(val parent: StandardRecyclerViewAdapter<T>) : ArrayList<(T) -> Unit>(), KObservableInterface<T> {
        lateinit var viewHolder: ViewHolder<T>
        val position: Int get() = viewHolder.adapterPosition

        override fun get(): T {
            if (position >= 0 && position < parent.list.size) {
                return parent.list[position]
            } else return parent.list.first()
        }

        override fun set(v: T): Unit {
            if (position < 0 || position >= parent.list.size) return
            val list = parent.list as? MutableList<T> ?: throw IllegalAccessException()
            list[position] = v
            update()
        }

        override fun update() {
            if (position >= 0 && position < parent.list.size) {
                runAll(parent.list[position])
            }
        }

    }

    class ViewHolder<T>(val itemView: View, val observable: ItemObservable<T>) : RecyclerView.ViewHolder(itemView)

    fun update(position: Int) {
        itemObservables[position].update()
    }

    class SRVAContext<T>(adapter: StandardRecyclerViewAdapter<T>, context: Context) : AnkoContextImpl<StandardRecyclerViewAdapter<T>>(context, adapter, false) {
        fun <V : View> V.lparams(
                width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                init: RecyclerView.LayoutParams.() -> Unit = {}
        ): V {
            val layoutParams = RecyclerView.LayoutParams(width, height)
            layoutParams.init()
            this@lparams.layoutParams = layoutParams

            return this
        }
    }
}

inline fun <T> RecyclerView.adapter(
        list: KObservableListInterface<T>,
        noinline makeView: AnkoContext<StandardRecyclerViewAdapter<T>>.(StandardRecyclerViewAdapter.ItemObservable<T>) -> Unit
): StandardRecyclerViewAdapter<T> {

    val newAdapter = StandardRecyclerViewAdapter(context, list, makeView)
    adapter = newAdapter
    list.onAdd.add { item, position ->
        adapter.notifyItemInserted(position)
    }
    list.onRemove.add { item, position ->
        adapter.notifyItemRemoved(position)
    }
    list.onChange.add { item, position ->
        //adapter.notifyItemChanged(position)
        newAdapter.update(position)
    }
    list.onReplace.add { list ->
        adapter.notifyDataSetChanged()
    }
    return newAdapter
}

inline fun <T> RecyclerView.makeAdapter(
        list: List<T>,
        noinline makeView: AnkoContext<StandardRecyclerViewAdapter<T>>.(StandardRecyclerViewAdapter.ItemObservable<T>) -> Unit
): StandardRecyclerViewAdapter<T> {
    val newAdapter = StandardRecyclerViewAdapter(context, list, makeView)
    adapter = newAdapter
    return newAdapter
}

inline fun <T> RecyclerView.makeAdapter(
        listObs: KObservable<List<T>>,
        noinline makeView: AnkoContext<StandardRecyclerViewAdapter<T>>.(StandardRecyclerViewAdapter.ItemObservable<T>) -> Unit
): StandardRecyclerViewAdapter<T> {
    val newAdapter = StandardRecyclerViewAdapter(context, listObs.get(), makeView)
    bind(listObs) {
        newAdapter.list = it
        newAdapter.notifyDataSetChanged()
    }
    adapter = newAdapter
    return newAdapter
}

fun RecyclerView.handlePaging(pagedEndpoint: PagedEndpoint<*>, kAdapter: StandardRecyclerViewAdapter<*>, pullingUpdate: (Boolean) -> Unit = {}) {
    var morePages = false
    bind(pagedEndpoint.isMoreObservable) { hasMore ->
        morePages = hasMore
    }
    bind(pagedEndpoint.pullingObservable) {
        pullingUpdate.invoke(it)
    }

    kAdapter.onScrollToBottom = {
        if (!pagedEndpoint.pulling && morePages) {
            pagedEndpoint.pull()
        }
    }
}