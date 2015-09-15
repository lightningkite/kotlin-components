package com.lightningkite.kotlincomponents.networking

import com.lightningkite.kotlincomponents.async.async
import com.squareup.okhttp.Headers
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.Response

/**
 * Created by jivie on 9/3/15.
 */
public class QuickNetworkingInterface(
        public val baseUrl: String,
        public val headers: Headers
) {
    public fun get(url: String, onResult: (Response) -> Unit): Unit = async({ Networking.syncGet(headers, baseUrl + url) }, onResult)
    public fun post(url: String, body: RequestBody, onResult: (Response) -> Unit): Unit = async({ Networking.syncPost(headers, baseUrl + url, body) }, onResult)
    public fun put(url: String, body: RequestBody, onResult: (Response) -> Unit): Unit = async({ Networking.syncPut(headers, baseUrl + url, body) }, onResult)
    public fun patch(url: String, body: RequestBody, onResult: (Response) -> Unit): Unit = async({ Networking.syncPatch(headers, baseUrl + url, body) }, onResult)
    public fun delete(url: String, body: RequestBody, onResult: (Response) -> Unit): Unit = async({ Networking.syncDelete(headers, baseUrl + url, body) }, onResult)
    public fun delete(url: String, onResult: (Response) -> Unit): Unit = async({ Networking.syncDelete(headers, baseUrl + url) }, onResult)
}