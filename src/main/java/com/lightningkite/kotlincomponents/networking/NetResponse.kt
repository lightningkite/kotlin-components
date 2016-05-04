package com.lightningkite.kotlincomponents.networking

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lightningkite.kotlincomponents.MyGson
import com.lightningkite.kotlincomponents.image.BitmapFactory_decodeByteArraySized
import org.json.JSONObject
import java.lang.reflect.Type

/**
 * Represents a response from the network.  It can be anything, so it's stored in a byte array.
 * Created by jivie on 9/23/15.
 */
class NetResponse(
        val code: Int,
        val raw: ByteArray,
        val request: NetRequest
) {
    val isSuccessful: Boolean get() = code / 100 == 2


    override fun toString(): String {
        return "NetResponse($request, $code, ${string()})"
    }

    fun string(): String {
        try {
            return raw.toString(Charsets.UTF_8)
        } catch(e: Exception) {
            return ""
        }
    }

    fun jsonElement(): JsonElement = JsonParser().parse(string())
    fun jsonObject(): JsonObject = JsonParser().parse(string()) as JsonObject
    fun toJSONObject(): JSONObject {
        try {
            return JSONObject(string())
        } catch(e: Exception) {
            e.printStackTrace()
            return JSONObject()
        }
    }

    inline fun <reified T : Any> gson(gson: Gson = MyGson.gson): T? {
        return gson.fromJson<T>(string())
    }

    fun <T : Any> gson(type: Class<T>, gson: Gson = MyGson.gson): T? {
        return gson.fromJson<T>(string(), type)
    }

    fun <T : Any> gson(type: Type, gson: Gson = MyGson.gson): T? {
        return gson.fromJson<T>(string(), type)
    }

    fun bitmap(options: BitmapFactory.Options = BitmapFactory.Options()): Bitmap? {
        try {
            return BitmapFactory.decodeByteArray(raw, 0, raw.size)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun bitmapSized(maxWidth: Int, maxHeight: Int): Bitmap? {
        try {
            return BitmapFactory_decodeByteArraySized(raw, 0, raw.size)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    inline fun <reified T : Any> auto(): T? {
        return when (T::class.java) {
            Unit::class.java -> Unit as T
            Bitmap::class.java -> bitmap() as T
            else -> gson<T>()
        }
    }
}