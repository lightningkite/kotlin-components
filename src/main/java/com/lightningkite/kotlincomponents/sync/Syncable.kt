package com.lightningkite.kotlincomponents.sync

import android.content.Context
import com.lightningkite.kotlincomponents.isNetworkAvailable

/**
 * Created by jivie on 4/5/16.
 */
interface Syncable {
    fun loadLocal()
    fun saveLocal()
    fun sync(onComplete: (List<SyncError>) -> Unit)

    fun attemptSync(context: Context, onComplete: (List<SyncError>) -> Unit) {
        if (context.isNetworkAvailable()) {
            sync(onComplete)
        } else {
            saveLocal()
        }
    }

    fun startup(context: Context, onComplete: (List<SyncError>) -> Unit) {
        loadLocal()
        if (context.isNetworkAvailable()) {
            sync(onComplete)
        } else {
            onComplete(listOf())
        }
    }
}