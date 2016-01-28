package com.lightningkite.kotlincomponents.networking

import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import java.nio.charset.Charset

/**
 * Created by jivie on 1/13/16.
 */
object OkHttpStack : NetStack {

    public val client: OkHttpClient by lazy(LazyThreadSafetyMode.NONE) { OkHttpClient() }

    override fun sync(method: NetMethod, url: String, body: NetBody, headers: Map<String, String>): NetResponse {
        try {
            val requestBuilder = Request.Builder().url(url)
            for ((key, value) in headers) requestBuilder.addHeader(key, value)
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
            val netResponse = NetResponse(response.code(), responseBody.bytes())
            responseBody.close()
            return netResponse

        } catch (e: Exception) {
            return NetResponse(0, e.message?.toByteArray(Charset.forName("UTF-8")) ?: "There was an exception.".toByteArray(Charset.forName("UTF-8")))
        }
    }

    private fun NetBody.toOkHttp(): RequestBody {
        if (this == NetBody.EMPTY) {
            return RequestBody.create(null, ByteArray(0))
        }
        return RequestBody.create(MediaType.parse(this.contentType.toString()), this.content)
    }
}