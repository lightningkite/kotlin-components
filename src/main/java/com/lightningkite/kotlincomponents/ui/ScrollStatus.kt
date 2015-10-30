package com.lightningkite.kotlincomponents.ui

import android.util.Log
import android.widget.AbsListView
import android.widget.ListView
import com.lightningkite.kotlincomponents.logging.logD

/**
 * Created by jivie on 10/23/15.
 */
class ScrollStatus(): AbsListView.OnScrollListener{

    private var _firstVisibleItem:Int = 0
    private var _visibleItemCount:Int = 0
    private var _totalItemCount:Int = 0

    public val firstVisibleItem:Int get() = _firstVisibleItem
    public val visibleItemCount:Int get() = _visibleItemCount
    public val totalItemCount:Int get() = _totalItemCount
    public val lastVisibleItem:Int get() = _firstVisibleItem + _visibleItemCount
    public val isAtBottom:Boolean get() {
        return _totalItemCount <= _firstVisibleItem + _visibleItemCount
    }
    public val isAtTop:Boolean get() = _firstVisibleItem == 0

    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        _firstVisibleItem = firstVisibleItem
        _visibleItemCount = visibleItemCount
        _totalItemCount = totalItemCount
    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
    }

}

public fun ListView.scrollStatus(): ScrollStatus{
    val listener = ScrollStatus()
    setOnScrollListener(listener)
    return listener
}