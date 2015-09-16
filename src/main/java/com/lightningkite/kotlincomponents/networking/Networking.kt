package com.lightningkite.kotlincomponents.networking

import com.lightningkite.kotlincomponents.async.async
import com.squareup.okhttp.*

/**
 * Created by jivie on 9/2/15.
 */

public object Networking {

    public val JSON: MediaType = MediaType.parse("application/json; charset=utf-8");
    public val HEADERS_EMPTY: Headers = Headers.Builder().build()

    public val client: OkHttpClient by Delegates.lazy { OkHttpClient() }

    public fun syncGet(headers: Headers, url: String): Response {
        val response = client.newCall(
                Request.Builder()
                        .headers(headers)
                        .url(url)
                        .build()
        ).execute()
        return response
    }

    public fun syncPost(headers: Headers, url: String, body: RequestBody): Response {
        val response = client.newCall(
                Request.Builder()
                        .headers(headers)
                        .url(url)
                        .post(body)
                        .build()
        ).execute()
        return response
    }

    public fun syncPut(headers: Headers, url: String, body: RequestBody): Response {
        val response = client.newCall(
                Request.Builder()
                        .headers(headers)
                        .url(url)
                        .put(body)
                        .build()
        ).execute()
        return response
    }

    public fun syncPatch(headers: Headers, url: String, body: RequestBody): Response {
        val response = client.newCall(
                Request.Builder()
                        .headers(headers)
                        .url(url)
                        .patch(body)
                        .build()
        ).execute()
        return response
    }

    public fun syncDelete(headers: Headers, url: String): Response {
        val response = client.newCall(
                Request.Builder()
                        .headers(headers)
                        .url(url)
                        .delete()
                        .build()
        ).execute()
        return response
    }

    public fun syncDelete(headers: Headers, url: String, body: RequestBody): Response {
        val response = client.newCall(
                Request.Builder()
                        .headers(headers)
                        .url(url)
                        .delete(body)
                        .build()
        ).execute()
        return response
    }

    public fun get(headers: Headers, url: String, onResult: (Response) -> Unit): Unit = async({ syncGet(headers, url) }, onResult)
    public fun post(headers: Headers, url: String, body: RequestBody, onResult: (Response) -> Unit): Unit = async({ syncPost(headers, url, body) }, onResult)
    public fun put(headers: Headers, url: String, body: RequestBody, onResult: (Response) -> Unit): Unit = async({ syncPut(headers, url, body) }, onResult)
    public fun patch(headers: Headers, url: String, body: RequestBody, onResult: (Response) -> Unit): Unit = async({ syncPatch(headers, url, body) }, onResult)
    public fun delete(headers: Headers, url: String, body: RequestBody, onResult: (Response) -> Unit): Unit = async({ syncDelete(headers, url, body) }, onResult)
    public fun delete(headers: Headers, url: String, onResult: (Response) -> Unit): Unit = async({ syncDelete(headers, url) }, onResult)

    public fun get(url: String, onResult: (Response) -> Unit): Unit = async({ syncGet(HEADERS_EMPTY, url) }, onResult)
    public fun post(url: String, body: RequestBody, onResult: (Response) -> Unit): Unit = async({ syncPost(HEADERS_EMPTY, url, body) }, onResult)
    public fun put(url: String, body: RequestBody, onResult: (Response) -> Unit): Unit = async({ syncPut(HEADERS_EMPTY, url, body) }, onResult)
    public fun patch(url: String, body: RequestBody, onResult: (Response) -> Unit): Unit = async({ syncPatch(HEADERS_EMPTY, url, body) }, onResult)
    public fun delete(url: String, body: RequestBody, onResult: (Response) -> Unit): Unit = async({ syncDelete(HEADERS_EMPTY, url, body) }, onResult)
    public fun delete(url: String, onResult: (Response) -> Unit): Unit = async({ syncDelete(HEADERS_EMPTY, url) }, onResult)
}