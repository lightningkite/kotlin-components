package com.lightningkite.kotlincomponents.networking

import com.squareup.okhttp.*
import java.nio.charset.Charset

/**
 * Created by jivie on 1/13/16.
 */
object OkHttpStack : NetStack {
    public val mediaType :MediaType = MediaType.parse("application/json")
    public val client: OkHttpClient by lazy(LazyThreadSafetyMode.NONE) { OkHttpClient() }

    /**
     * Synchronously makes an HTTP GET request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     */
    override fun syncGet(headers: Headers, url: String): NetResponse {
        try {
            val response = client.newCall(
                    Request.Builder()
                            .headers(headers)
                            .url(url)
                            .build()
            ).execute()
            val responseBody = response.body()
            val netResponse = NetResponse(response.code(), responseBody.bytes())
            responseBody.close()
            return netResponse
        } catch(e: Exception) {
            return NetResponse(0, e.message?.toByteArray(Charset.forName("UTF-8")) ?: "There was an exception.".toByteArray(Charset.forName("UTF-8")))
        }
    }

    /**
     * Synchronously makes an HTTP POST request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    override fun syncPost(headers: Headers, url: String, body: String): NetResponse {
        try {
            val response = client.newCall(
                    Request.Builder()
                            .headers(headers)
                            .url(url)
                            .post(RequestBody.create(mediaType, body))
                            .build()
            ).execute()
            val responseBody = response.body()
            val netResponse = NetResponse(response.code(), responseBody.bytes())
            responseBody.close()
            return netResponse
        } catch(e: Exception) {
            return NetResponse(0, e.message?.toByteArray(Charset.forName("UTF-8")) ?: "{\"error\":\"There was an exception.\"}".toByteArray(Charset.forName("UTF-8")))
        }
    }

    /**
     * Synchronously makes an HTTP PUT request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    override fun syncPut(headers: Headers, url: String, body: String): NetResponse {
        try {
            val response = client.newCall(
                    Request.Builder()
                            .headers(headers)
                            .url(url)
                            .put(RequestBody.create(mediaType, body))
                            .build()
            ).execute()
            val responseBody = response.body()
            val netResponse = NetResponse(response.code(), responseBody.bytes())
            responseBody.close()
            return netResponse
        } catch(e: Exception) {
            return NetResponse(0, e.message?.toByteArray(Charset.forName("UTF-8")) ?: "{\"error\":\"There was an exception.\"}".toByteArray(Charset.forName("UTF-8")))
        }
    }

    /**
     * Synchronously makes an HTTP PATCH request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    override fun syncPatch(headers: Headers, url: String, body: String): NetResponse {
        try {
            val response = client.newCall(
                    Request.Builder()
                            .headers(headers)
                            .url(url)
                            .patch(RequestBody.create(mediaType, body))
                            .build()
            ).execute()
            val responseBody = response.body()
            val netResponse = NetResponse(response.code(), responseBody.bytes())
            responseBody.close()
            return netResponse
        } catch(e: Exception) {
            return NetResponse(0, e.message?.toByteArray(Charset.forName("UTF-8")) ?: "{\"error\":\"There was an exception.\"}".toByteArray(Charset.forName("UTF-8")))
        }
    }

    /**
     * Synchronously makes an HTTP DELETE request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    override fun syncDelete(headers: Headers, url: String, body: String): NetResponse {
        try {
            val response = client.newCall(
                    Request.Builder()
                            .headers(headers)
                            .url(url)
                            .delete(RequestBody.create(mediaType, ""))
                            .build()
            ).execute()
            val responseBody = response.body()
            val netResponse = NetResponse(response.code(), responseBody.bytes())
            responseBody.close()
            return netResponse
        } catch(e: Exception) {
            return NetResponse(0, e.message?.toByteArray(Charset.forName("UTF-8")) ?: "{\"error\":\"There was an exception.\"}".toByteArray(Charset.forName("UTF-8")))
        }
    }
}