package com.lightningkite.kotlincomponents.networking

import com.lightningkite.kotlincomponents.async.doAsync
import com.squareup.okhttp.*
import java.nio.charset.Charset

/**
 * Created by jivie on 9/2/15.
 */

public object Networking {

    public val JSON: MediaType = MediaType.parse("application/json; charset=utf-8");
    public val HEADERS_EMPTY: Headers = Headers.Builder().build()

    public val client: OkHttpClient by lazy(LazyThreadSafetyMode.NONE) { OkHttpClient() }

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
            return NetResponse(0, e.getMessage()?.toByteArray(Charset.forName("UTF-8")) ?: "There was an exception.".toByteArray(Charset.forName("UTF-8")))
        }
    }

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
            return NetResponse(0, e.getMessage()?.toByteArray(Charset.forName("UTF-8")) ?: "{\"error\":\"There was an exception.\"}".toByteArray(Charset.forName("UTF-8")))
        }
    }

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
            return NetResponse(0, e.getMessage()?.toByteArray(Charset.forName("UTF-8")) ?: "{\"error\":\"There was an exception.\"}".toByteArray(Charset.forName("UTF-8")))
        }
    }

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
            return NetResponse(0, e.getMessage()?.toByteArray(Charset.forName("UTF-8")) ?: "{\"error\":\"There was an exception.\"}".toByteArray(Charset.forName("UTF-8")))
        }
    }

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
            return NetResponse(0, e.getMessage()?.toByteArray(Charset.forName("UTF-8")) ?: "{\"error\":\"There was an exception.\"}".toByteArray(Charset.forName("UTF-8")))
        }
    }

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
            return NetResponse(0, e.getMessage()?.toByteArray(Charset.forName("UTF-8")) ?: "{\"error\":\"There was an exception.\"}".toByteArray(Charset.forName("UTF-8")))
        }
    }

    public fun get(headers: Headers, url: String, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncGet(headers, url) }, onResult)
    public fun post(headers: Headers, url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPost(headers, url, body) }, onResult)
    public fun put(headers: Headers, url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPut(headers, url, body) }, onResult)
    public fun patch(headers: Headers, url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPatch(headers, url, body) }, onResult)
    public fun delete(headers: Headers, url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncDelete(headers, url, body) }, onResult)
    public fun delete(headers: Headers, url: String, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncDelete(headers, url) }, onResult)

    public fun get(url: String, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncGet(HEADERS_EMPTY, url) }, onResult)
    public fun post(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPost(HEADERS_EMPTY, url, body) }, onResult)
    public fun put(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPut(HEADERS_EMPTY, url, body) }, onResult)
    public fun patch(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPatch(HEADERS_EMPTY, url, body) }, onResult)
    public fun delete(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncDelete(HEADERS_EMPTY, url, body) }, onResult)
    public fun delete(url: String, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncDelete(HEADERS_EMPTY, url) }, onResult)
}