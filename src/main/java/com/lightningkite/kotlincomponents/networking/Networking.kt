package com.lightningkite.kotlincomponents.networking

import com.lightningkite.kotlincomponents.async.doAsync
import com.squareup.okhttp.*
import java.nio.charset.Charset

/**
 * Used to make network calls easier.
 * Created by jivie on 9/2/15.
 */

public object Networking {

    public val JSON: MediaType = MediaType.parse("application/json; charset=utf-8");
    public val HEADERS_EMPTY: Headers = Headers.Builder().build()

    public val client: OkHttpClient by lazy(LazyThreadSafetyMode.NONE) { OkHttpClient() }

    /**
     * Synchronously makes an HTTP GET request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     */
    public fun syncGet(headers: Headers, url: String): NetResponse {
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
    public fun syncPost(headers: Headers, url: String, body: RequestBody): NetResponse {
        try {
            val response = client.newCall(
                    Request.Builder()
                            .headers(headers)
                            .url(url)
                            .post(body)
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
    public fun syncPut(headers: Headers, url: String, body: RequestBody): NetResponse {
        try {
            val response = client.newCall(
                    Request.Builder()
                            .headers(headers)
                            .url(url)
                            .put(body)
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
    public fun syncPatch(headers: Headers, url: String, body: RequestBody): NetResponse {
        try {
            val response = client.newCall(
                    Request.Builder()
                            .headers(headers)
                            .url(url)
                            .patch(body)
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
     */
    public fun syncDelete(headers: Headers, url: String): NetResponse {
        try {
            val response = client.newCall(
                    Request.Builder()
                            .headers(headers)
                            .url(url)
                            .delete()
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
    public fun syncDelete(headers: Headers, url: String, body: RequestBody): NetResponse {
        try {
            val response = client.newCall(
                    Request.Builder()
                            .headers(headers)
                            .url(url)
                            .delete(body)
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
     * Asynchronously makes an HTTP GET request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     */
    public fun get(headers: Headers, url: String, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncGet(headers, url) }, onResult)

    /**
     * Asynchronously makes an HTTP POST request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun post(headers: Headers, url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPost(headers, url, body) }, onResult)

    /**
     * Asynchronously makes an HTTP PUT request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun put(headers: Headers, url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPut(headers, url, body) }, onResult)

    /**
     * Asynchronously makes an HTTP PATCH request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun patch(headers: Headers, url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPatch(headers, url, body) }, onResult)

    /**
     * Asynchronously makes an HTTP DELETE request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun delete(headers: Headers, url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncDelete(headers, url, body) }, onResult)

    /**
     * Asynchronously makes an HTTP DELETE request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     */
    public fun delete(headers: Headers, url: String, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncDelete(headers, url) }, onResult)


    /**
     * Asynchronously makes an HTTP GET request with empty headers.
     * @param url The URL the request is made to.
     */
    public fun get(url: String, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncGet(HEADERS_EMPTY, url) }, onResult)

    /**
     * Asynchronously makes an HTTP POST request with empty headers.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun post(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPost(HEADERS_EMPTY, url, body) }, onResult)

    /**
     * Asynchronously makes an HTTP PUT request with empty headers.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun put(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPut(HEADERS_EMPTY, url, body) }, onResult)

    /**
     * Asynchronously makes an HTTP PATCH request with empty headers.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun patch(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPatch(HEADERS_EMPTY, url, body) }, onResult)

    /**
     * Asynchronously makes an HTTP DELETE request with empty headers.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun delete(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncDelete(HEADERS_EMPTY, url, body) }, onResult)

    /**
     * Asynchronously makes an HTTP DELETE request with empty headers.
     * @param url The URL the request is made to.
     */
    public fun delete(url: String, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncDelete(HEADERS_EMPTY, url) }, onResult)
}