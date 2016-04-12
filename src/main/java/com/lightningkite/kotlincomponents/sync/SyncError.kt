package com.lightningkite.kotlincomponents.sync

import com.lightningkite.kotlincomponents.networking.NetResponse

/**
 * Created by jivie on 4/12/16.
 */
class SyncError(
        var message: String = "?",
        @Transient var change: ItemChange<*, *>? = null,
        @Transient var response: NetResponse? = null
) {
    override fun toString(): String {
        return "SyncError(" + message + ", " + response?.code?.toString() + ", " + response?.string() + ")"
    }
}