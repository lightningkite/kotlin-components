package com.lightningkite.kotlincomponents.sync

/**
 * Created by jivie on 4/12/16.
 */
class PullResult<T>(
        val list: List<T>? = null,
        val error: SyncError? = null
) {
    constructor(error: SyncError? = null) : this(null, error)
}