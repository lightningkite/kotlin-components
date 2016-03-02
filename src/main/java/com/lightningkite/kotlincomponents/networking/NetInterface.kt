package com.lightningkite.kotlincomponents.networking

import java.util.*

/**
 * Created by jivie on 2/26/16.
 */

open class NetInterface {
    val customStack: NetStack? = null
    val stack: NetStack get() = customStack ?: Networking.stack
    val defaultHeaders: Map<String, String> = mapOf()
    val onError = ArrayList<(NetResponse) -> Unit>()

    fun endpoint(url: String) = NetEndpoint.fromUrl(this, url)
}