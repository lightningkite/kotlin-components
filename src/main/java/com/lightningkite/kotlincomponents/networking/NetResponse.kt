package com.lightningkite.kotlincomponents.networking

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.lightningkite.kotlincomponents.BasicGson

/**
 * Created by jivie on 9/23/15.
 */
public data class NetResponse(
        public val code: Int,
        public val body: ByteArray
) {
    public val isSuccessful: Boolean get() = code / 100 == 2
    public inline fun <reified T : Any> result(gson: Gson = BasicGson.gson): T? {
        return gson.fromJson<T>(body.toString("UTF-8"))
    }
}