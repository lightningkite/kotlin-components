package com.lightningkite.kotlincomponents.networking

import com.google.gson.Gson
import com.lightningkite.kotlincomponents.BasicGson
import com.lightningkite.kotlincomponents.async.doAsync
import com.squareup.okhttp.Headers

/**
 * Created by jivie on 9/14/15.
 */
public interface NetModel {

    public val url: String
    public val headers: Headers get() = Networking.HEADERS_EMPTY
    public val gson: Gson get() = BasicGson.gson

    public fun netPost(onResult: (NetResponse) -> Unit): Unit = doAsync({ Networking.syncPost(headers, url, this.gsonToRequestBody(gson)) }, onResult)
    public fun netPut(onResult: (NetResponse) -> Unit): Unit = doAsync({ Networking.syncPut(headers, url, this.gsonToRequestBody(gson)) }, onResult)
    public fun netPatch(onResult: (NetResponse) -> Unit): Unit = doAsync({ Networking.syncPatch(headers, url, this.gsonToRequestBody(gson)) }, onResult)
    public fun netDeleteWithBody(onResult: (NetResponse) -> Unit): Unit = doAsync({ Networking.syncDelete(headers, url, this.gsonToRequestBody(gson)) }, onResult)
    public fun netDelete(onResult: (NetResponse) -> Unit): Unit = doAsync({ Networking.syncDelete(headers, url) }, onResult)
}