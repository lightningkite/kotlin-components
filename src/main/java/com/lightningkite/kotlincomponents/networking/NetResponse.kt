package com.lightningkite.kotlincomponents.networking

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lightningkite.kotlincomponents.BasicGson

/**
 * Created by jivie on 9/23/15.
 */
public data class NetResponse(
        public val code: Int,
        public val rawBody: ByteArray
) {
    public val isSuccessful: Boolean get() = code / 100 == 2
    public inline fun <reified T : Any> result(gson: Gson = BasicGson.gson): T? {
        return gson.fromJson<T>(string())
    }

    public fun string(): String = rawBody.toString("UTF-8")
    public fun jsonElement(): JsonElement = JsonParser().parse(string())
    public fun jsonObject(): JsonObject = JsonParser().parse(string()) as JsonObject
}