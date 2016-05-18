package com.lightningkite.kotlincomponents.networking.paged

import android.support.v7.widget.RecyclerView
import com.lightningkite.kotlincomponents.adapter.StandardRecyclerViewAdapter

/**
 * Created by jivie on 5/18/16.
 */
fun RecyclerView.handlePaging(pagedEndpoint: PagedListManager<*>, kAdapter: StandardRecyclerViewAdapter<*>, onError: () -> Unit) {
    kAdapter.onScrollToBottom = {
        pagedEndpoint.pull { message, response -> onError() }
    }
}