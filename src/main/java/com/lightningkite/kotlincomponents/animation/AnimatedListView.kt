package com.lightningkite.kotlincomponents.animation

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ListAdapter
import android.widget.ListView
import org.jetbrains.anko.layoutParams
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.visibility

/**
 * Created by jivie on 8/7/15.
 */
public class AnimatedListView(context: Context) : FrameLayout(context) {
    private var front: ListView = ListView(context)
    private var back: ListView = ListView(context)

    private var _onItemClick: (parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) -> Unit = { a, b, c, d -> }
    public var onItemClick: (parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) -> Unit
        get() = _onItemClick
        set(value) {
            _onItemClick = value
            front.setOnItemClickListener(value)
        }
    private var _onItemLongClick: (parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) -> Boolean = { a, b, c, d -> true }
    public var onItemLongClick: (parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) -> Boolean
        get() = _onItemLongClick
        set(value) {
            _onItemLongClick = value
            front.setOnItemLongClickListener(value)
        }

    init {
        back.layoutParams = FrameLayout.LayoutParams(matchParent, matchParent)
        back.visibility = View.GONE
        front.layoutParams = FrameLayout.LayoutParams(matchParent, matchParent)
        addView(back)
        addView(front)
    }

    public fun setAdapter(adapter: ListAdapter?, animationSet: AnimationSet = AnimationSet.fade) {
        val temp = front
        front = back
        back = temp

        back.setOnItemClickListener(null)
        front.setOnItemClickListener(_onItemClick)

        back.setOnItemLongClickListener(null)
        front.setOnItemLongClickListener(_onItemLongClick)

        back.(animationSet.animateOut)(this).withEndAction {
            back.visibility = View.GONE
        }.start()

        front.setAdapter(adapter)
        front.visibility = View.VISIBLE
        front.(animationSet.animateIn)(this).start()
    }

    public fun getAdapter(): ListAdapter = front.getAdapter()
}