package com.lightningkite.kotlincomponents.networking

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.lightningkite.kotlincomponents.BasicGson
import com.squareup.okhttp.Headers
import com.squareup.okhttp.Response

/**
 * Created by jivie on 9/3/15.
 */
public inline fun <reified T> Response.gsonFrom(gson: Gson = BasicGson.gson): T? {
    return gson.fromJson<T>(body().string())
}

public fun headers(vararg test: Pair<String, String>): Headers {
    val builder = Headers.Builder()
    test.forEach { builder.add(it.first, it.second) }
    return builder.build()
}