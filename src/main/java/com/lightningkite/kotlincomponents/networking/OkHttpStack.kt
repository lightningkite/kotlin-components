package com.lightningkite.kotlincomponents.networking

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.lightningkite.kotlincomponents.async.doAsync
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import java.nio.charset.Charset

/**
 * Created by jivie on 1/13/16.
 */
object OkHttpStack : NetStack {

    fun imageSync(url: String, minBytes: Long): Bitmap? {
        try {
            val response = client.newCall(Request.Builder().url(url).build()).execute()
            val body = response.body()
            val opts = BitmapFactory.Options().apply {
                inSampleSize = (body.contentLength() / minBytes).toInt()
            }
            return BitmapFactory.decodeStream(body.byteStream(), null, opts)
        } catch(e: Exception) {
            return null
        }
    }

    fun imageSync(url: String): Bitmap? {
        try {
            val response = client.newCall(Request.Builder().url(url).build()).execute()
            val body = response.body()
            val opts = BitmapFactory.Options()
            return BitmapFactory.decodeStream(body.byteStream(), null, opts)
        } catch(e: Exception) {
            return null
        }
    }

    fun image(url: String, minBytes: Long, onResult: (Bitmap?) -> Unit) {
        doAsync({ imageSync(url, minBytes) }, onResult)
    }

    fun image(url: String, onResult: (Bitmap?) -> Unit) {
        doAsync({ imageSync(url) }, onResult)
    }

    val client: OkHttpClient by lazy(LazyThreadSafetyMode.NONE) { OkHttpClient() }

    override fun sync(method: NetMethod, url: String, body: NetBody, headers: Map<String, String>): NetResponse {
        try {
            val requestBuilder = Request.Builder().url(url)
            for ((key, value) in headers) {
                requestBuilder.addHeader(key, value)
            }
            when (method) {
                NetMethod.GET -> {
                }
                NetMethod.POST -> requestBuilder.post(body.toOkHttp())
                NetMethod.PUT -> requestBuilder.put(body.toOkHttp())
                NetMethod.PATCH -> requestBuilder.patch(body.toOkHttp())
                NetMethod.DELETE -> requestBuilder.delete(body.toOkHttp())
                else -> {
                    throw IllegalArgumentException("Unknown NetMethod.")
                }
            }
            val response = client.newCall(requestBuilder.build()).execute()
            val responseBody = response.body()
            val netResponse = NetResponse(response.code(), responseBody.bytes(), method, url, body, headers)
            responseBody.close()
            return netResponse

        } catch (e: Exception) {
            return NetResponse(0, e.message?.toByteArray(Charset.forName("UTF-8")) ?: "There was an exception.".toByteArray(Charset.forName("UTF-8")), method, url, body, headers)
        }
    }

    private fun NetBody.toOkHttp(): RequestBody {
        if (this == NetBody.EMPTY) {
            return RequestBody.create(null, ByteArray(0))
        }
        return RequestBody.create(MediaType.parse(this.contentType.toString()), this.content)
    }
}