package com.lightningkite.kotlincomponents.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlincomponents.networking.PagedEndpoint
import com.lightningkite.kotlincomponents.observable.*
import com.lightningkite.kotlincomponents.runAll
import org.jetbrains.anko.AnkoContextImpl
import java.util.*

/**
 * An adapter for RecyclerViews intended to be used in all cases.
 *
 * Created by jivie on 5/4/16.
 */
class StandardRecyclerViewAdapter<T>(
        val context: Context,
        initialList: List<T>,
        val makeView: SRVAContext<T>.(ItemObservable<T>) -> Unit
) : RecyclerView.Adapter<StandardRecyclerViewAdapter.ViewHolder<T>>() {

    var list: List<T> = initialList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var previousListenerSet: KObservableListListenerSet<T>? = null

    fun attachAnimations(list: KObservableListInterface<T>) {
        detatchAnimations()
        previousListenerSet = KObservableListListenerSet(
                onAddListener = { item: T, position: Int ->
                    notifyItemInserted(position)
                },
                onRemoveListener = { item: T, position: Int ->
                    notifyItemRemoved(position)
                },
                onChangeListener = { item: T, position: Int ->
                    //adapter.notifyItemChanged(position)
                    update(position)
                },
                onReplaceListener = { list: KObservableListInterface<T> ->
                    notifyDataSetChanged()
                }
        )
        list.addListenerSet(previousListenerSet!!)
    }

    fun detatchAnimations() {
        if (previousListenerSet != null) {
            (list as? KObservableListInterface<T>)?.removeListenerSet(previousListenerSet!!)
        }
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
        var viewHolder: ViewHolder<T>? = null
        val position: Int get() = viewHolder?.adapterPosition ?: 0

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

@Deprecated("Use standardAdapter() instead.", ReplaceWith("standardAdapter(list, makeView)", "com.lightningkite.kotlincomponents.adapter.standardAdapter"))
inline fun <T> RecyclerView.adapter(
        list: List<T>,
        noinline makeView: StandardRecyclerViewAdapter.SRVAContext<T>.(StandardRecyclerViewAdapter.ItemObservable<T>) -> Unit
): StandardRecyclerViewAdapter<T> = standardAdapter(list, makeView)

inline fun <T> RecyclerView.standardAdapter(
        list: List<T>,
        noinline makeView: StandardRecyclerViewAdapter.SRVAContext<T>.(StandardRecyclerViewAdapter.ItemObservable<T>) -> Unit
): StandardRecyclerViewAdapter<T> {
    val newAdapter = StandardRecyclerViewAdapter(context, list, makeView)
    adapter = newAdapter
    return newAdapter
}

@Deprecated("Use standardAdapter() instead.", ReplaceWith("standardAdapter(list, makeView)", "com.lightningkite.kotlincomponents.adapter.standardAdapter"))
inline fun <T> RecyclerView.adapter(
        list: KObservableListInterface<T>,
        noinline makeView: StandardRecyclerViewAdapter.SRVAContext<T>.(StandardRecyclerViewAdapter.ItemObservable<T>) -> Unit
): StandardRecyclerViewAdapter<T> = standardAdapter(list, makeView)

inline fun <T> RecyclerView.standardAdapter(
        list: KObservableListInterface<T>,
        noinline makeView: StandardRecyclerViewAdapter.SRVAContext<T>.(StandardRecyclerViewAdapter.ItemObservable<T>) -> Unit
): StandardRecyclerViewAdapter<T> {
    val newAdapter = StandardRecyclerViewAdapter(context, list, makeView)
    adapter = newAdapter
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
            newAdapter.attachAnimations(list)
        }

        override fun onViewDetachedFromWindow(v: View?) {
            newAdapter.detatchAnimations()
        }

    })
    return newAdapter
}

@Deprecated("Use standardAdapter() instead.", ReplaceWith("standardAdapter(list, makeView)", "com.lightningkite.kotlincomponents.adapter.standardAdapter"))
inline fun <T> RecyclerView.adapterObservable(
        listObs: KObservableInterface<List<T>>,
        noinline makeView: StandardRecyclerViewAdapter.SRVAContext<T>.(StandardRecyclerViewAdapter.ItemObservable<T>) -> Unit
): StandardRecyclerViewAdapter<T> = standardAdapterObservable(listObs, makeView)

inline fun <T> RecyclerView.standardAdapterObservable(
        listObs: KObservableInterface<List<T>>,
        noinline makeView: StandardRecyclerViewAdapter.SRVAContext<T>.(StandardRecyclerViewAdapter.ItemObservable<T>) -> Unit
): StandardRecyclerViewAdapter<T> {
    val newAdapter = StandardRecyclerViewAdapter(context, listObs.get(), makeView)
    listen(listObs) {
        newAdapter.list = it
        if (it is KObservableListInterface<T>) {
            newAdapter.attachAnimations(it)
        }
    }
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
            val list = listObs.get()
            if (list is KObservableListInterface<T>) {
                newAdapter.attachAnimations(list)
            }
        }

        override fun onViewDetachedFromWindow(v: View?) {
            newAdapter.detatchAnimations()
        }

    })
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

fun <T : Any> RecyclerView.handlePagingObservable(pagedEndpointObs: KObservableInterface<PagedEndpoint<T>>, kAdapter: StandardRecyclerViewAdapter<*>, pullingUpdate: (Boolean) -> Unit = {}) {
    var morePages = false
    bindSub(pagedEndpointObs, { it.isMoreObservable }) { hasMore ->
        morePages = hasMore
    }
    bindSub(pagedEndpointObs, { it.pullingObservable }) {
        pullingUpdate.invoke(it)
    }

    kAdapter.onScrollToBottom = {
        if (!pagedEndpointObs.get().pulling && morePages) {
            pagedEndpointObs.get().pull()
        }
    }
}

fun <T : Any> RecyclerView.handlePagingOptionalObservable(pagedEndpointObs: KObservableInterface<PagedEndpoint<T>?>, kAdapter: StandardRecyclerViewAdapter<*>, pullingUpdate: (Boolean) -> Unit = {}) {
    var morePages = false
    bindSub(pagedEndpointObs, { it?.isMoreObservable ?: KObservable(true) }) { hasMore ->
        morePages = hasMore
    }
    bindSub(pagedEndpointObs, { it?.pullingObservable ?: KObservable(true) }) {
        pullingUpdate.invoke(it)
    }

    kAdapter.onScrollToBottom = {
        if (!(pagedEndpointObs.get()?.pulling ?: true) && morePages) {
            pagedEndpointObs.get()?.pull()
        }
    }
}