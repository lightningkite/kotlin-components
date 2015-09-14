package com.lightningkite.kotlincomponents.networking

import com.google.gson.Gson
import com.lightningkite.kotlincomponents.BasicGson
import com.lightningkite.kotlincomponents.async.async
import com.lightningkite.kotlincomponents.gsonTo
import com.squareup.okhttp.Headers
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.Response

/**
 * Created by jivie on 9/14/15.
 */
public interface NetModel {

    public val url: String
    public val headers: Headers get() = Networking.HEADERS_EMPTY
    public val gson: Gson get() = BasicGson.gson

    public fun netPost(onResult: (Response) -> Unit): Unit = async({ Networking.syncPost(headers, url, RequestBody.create(Networking.JSON, this.gsonTo(gson))) }, onResult)
    public fun netPut(onResult: (Response) -> Unit): Unit = async({ Networking.syncPut(headers, url, RequestBody.create(Networking.JSON, this.gsonTo(gson))) }, onResult)
    public fun netPatch(onResult: (Response) -> Unit): Unit = async({ Networking.syncPatch(headers, url, RequestBody.create(Networking.JSON, this.gsonTo(gson))) }, onResult)
    public fun netDeleteWithBody(onResult: (Response) -> Unit): Unit = async({ Networking.syncDelete(headers, url, RequestBody.create(Networking.JSON, this.gsonTo(gson))) }, onResult)
    public fun netDelete(onResult: (Response) -> Unit): Unit = async({ Networking.syncDelete(headers, url) }, onResult)
}