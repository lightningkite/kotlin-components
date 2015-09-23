package com.lightningkite.kotlincomponents.networking

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lightningkite.kotlincomponents.BasicGson
import com.squareup.okhttp.Headers
import com.squareup.okhttp.RequestBody

/**
 * Created by jivie on 9/3/15.
 */
public inline fun <reified T : Any> NetResponse.gsonTo(gson: Gson = BasicGson.gson): T? {
    return gson.fromJson<T>(body.toString("UTF-8"))
}

public inline fun <reified T : Any> T.gsonToRequestBody(gson: Gson = BasicGson.gson): RequestBody {
    return RequestBody.create(Networking.JSON, gson.toJson(this))
}

public fun JsonObject.toRequestBody(): RequestBody {
    return RequestBody.create(Networking.JSON, toString())
}

public fun headers(vararg test: Pair<String, String>): Headers {
    val builder = Headers.Builder()
    test.forEach { builder.add(it.first, it.second) }
    return builder.build()
}