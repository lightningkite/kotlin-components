package com.lightningkite.kotlincomponents.networking

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lightningkite.kotlincomponents.BasicGson
import org.json.JSONObject

/**
 * Represents a response from the network.  It can be anything, so it's stored in a byte array.
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

    public fun bitmap(): Bitmap? {
        try {
            return BitmapFactory.decodeByteArray(rawBody, 0, rawBody.size)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    public fun string(): String {
        try {
            return rawBody.toString("UTF-8")
        } catch(e: Exception) {
            return ""
        }
    }
    public fun jsonElement(): JsonElement = JsonParser().parse(string())
    public fun jsonObject(): JsonObject = JsonParser().parse(string()) as JsonObject
    public fun toJSONObject(): JSONObject {
        try {
            return JSONObject(string())
        } catch(e: Exception) {
            e.printStackTrace()
            return JSONObject()
        }
    }
}