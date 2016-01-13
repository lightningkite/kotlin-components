package com.lightningkite.kotlincomponents.networking

import com.lightningkite.kotlincomponents.async.doAsync
import com.squareup.okhttp.*
import java.nio.charset.Charset

/**
 * Used to make network calls easier.
 * Created by jivie on 9/2/15.
 */

public object Networking: NetStack {

    var stack: NetStack = OkHttpNetworking

    public val JSON: MediaType = MediaType.parse("application/json; charset=utf-8");
    public val HEADERS_EMPTY: Headers = Headers.Builder().build()

    override fun syncGet(headers: Headers, url: String): NetResponse = stack.syncGet(headers, url)
    override fun syncPost(headers: Headers, url: String, body: RequestBody): NetResponse = stack.syncPost(headers, url, body)
    override fun syncPut(headers: Headers, url: String, body: RequestBody): NetResponse = stack.syncPut(headers, url, body)
    override fun syncPatch(headers: Headers, url: String, body: RequestBody): NetResponse = stack.syncPatch(headers, url, body)
    override fun syncDelete(headers: Headers, url: String, body: RequestBody): NetResponse = stack.syncDelete(headers, url, body)
}