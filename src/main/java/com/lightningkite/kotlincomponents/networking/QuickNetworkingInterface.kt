package com.lightningkite.kotlincomponents.networking

import com.lightningkite.kotlincomponents.async.doAsync
import com.squareup.okhttp.Headers
import com.squareup.okhttp.RequestBody

/**
 * Give you all of the networking calls, but using the same set of headers across all of them.
 * Created by jivie on 9/3/15.
 */
public open class QuickNetworkingInterface(
        public val baseUrl: String,
        public val headers: Headers
) {
    public fun get(url: String, onResult: (NetResponse) -> Unit): Unit = doAsync({ Networking.syncGet(headers, baseUrl + url) }, onResult)
    public fun post(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ Networking.syncPost(headers, baseUrl + url, body) }, onResult)
    public fun put(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ Networking.syncPut(headers, baseUrl + url, body) }, onResult)
    public fun patch(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ Networking.syncPatch(headers, baseUrl + url, body) }, onResult)
    public fun delete(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ Networking.syncDelete(headers, baseUrl + url, body) }, onResult)
    public fun delete(url: String, onResult: (NetResponse) -> Unit): Unit = doAsync({ Networking.syncDelete(headers, baseUrl + url) }, onResult)

    public fun syncGet(url: String): NetResponse = Networking.syncGet(headers, baseUrl + url)
    public fun syncPost(url: String, body: RequestBody): NetResponse = Networking.syncPost(headers, baseUrl + url, body)
    public fun syncPut(url: String, body: RequestBody): NetResponse = Networking.syncPut(headers, baseUrl + url, body)
    public fun syncPatch(url: String, body: RequestBody): NetResponse = Networking.syncPatch(headers, baseUrl + url, body)
    public fun syncDelete(url: String, body: RequestBody): NetResponse = Networking.syncDelete(headers, baseUrl + url, body)
    public fun syncDelete(url: String): NetResponse = Networking.syncDelete(headers, baseUrl + url)
}