package com.lightningkite.kotlincomponents.networking

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lightningkite.kotlincomponents.MyGson
import com.lightningkite.kotlincomponents.toByteArray
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type

/**
 * Represents a response from the network.
 * It can only be read from once, as it really just holds a stream.  Don't screw it up.
 * Created by jivie on 9/23/15.
 */
open class NetStream(
        val code: Int,
        val length: Long,
        val type: NetContentType,
        val stream: InputStream,
        val request: NetRequest
) {

    open fun open() {
        if (closed) throw IllegalStateException("Stream already closed.")
    }

    var closed = false
    open fun close() {
        stream.close()
        closed = true
    }

    inline fun <T> readStream(action: (stream: InputStream) -> T): T {
        try {
            open()
            val result = action(stream)
            return result
        } finally {
            close()
        }
    }

    companion object {
        fun fromByteArray(
                code: Int,
                array: ByteArray,
                request: NetRequest,
                type: NetContentType = NetContentType.JSON
        ): NetStream = NetStream(code, array.size.toLong(), type, ByteArrayInputStream(array), request)
    }

    val isSuccessful: Boolean get() = code / 100 == 2

    fun response(): NetResponse {
        return NetResponse(code, raw(), request)
    }

    fun raw(): ByteArray = readStream { it.toByteArray() }

    fun bitmap(options: BitmapFactory.Options = BitmapFactory.Options()): Bitmap? {
        val opts = BitmapFactory.Options()
        try {
            return readStream {
                BitmapFactory.decodeStream(it, null, opts)
            }
        } catch(e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun bitmapSized(minBytes: Long): Bitmap? {
        val opts = BitmapFactory.Options().apply {
            inSampleSize = (length / minBytes).toInt()
        }
        try {
            return readStream {
                BitmapFactory.decodeStream(it, null, opts)
            }
        } catch(e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun toString(): String {
        return "NetStream - Code: $code, Length: $length, Type: $type"
    }

    fun string(): String {
        try {
            return raw().toString(Charsets.UTF_8)
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
        return readStream {
            gson.fromJson<T>(InputStreamReader(it))
        }
    }

    fun <T : Any> gson(type: Type, gson: Gson = MyGson.gson): T? {
        return readStream {
            gson.fromJson<T>(InputStreamReader(it), type)
        }
    }

    inline fun <reified T : Any> auto(): T? {
        return when (T::class.java) {
            Unit::class.java -> {
                readStream { }
                Unit as T
            }
            Bitmap::class.java -> bitmap() as T
            else -> gson<T>()
        }
    }
}