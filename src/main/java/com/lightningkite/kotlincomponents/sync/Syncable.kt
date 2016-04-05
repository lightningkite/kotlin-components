package com.lightningkite.kotlincomponents.sync

import android.content.Context
import com.lightningkite.kotlincomponents.isNetworkAvailable

/**
 * Created by jivie on 4/5/16.
 */
interface Syncable {
    fun loadLocal()
    fun saveLocal()
    fun sync(onComplete: () -> Unit)

    fun attemptSync(context: Context, onComplete: () -> Unit) {
        if (context.isNetworkAvailable()) {
            sync(onComplete)
        }
    }

    fun startup(context: Context, onComplete: () -> Unit) {
        if (context.isNetworkAvailable()) {
            sync(onComplete)
        } else {
            loadLocal()
            onComplete()
        }
    }
}