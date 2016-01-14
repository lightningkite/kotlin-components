package com.lightningkite.kotlincomponents.networking

import com.lightningkite.kotlincomponents.async.doAsync
import com.squareup.okhttp.Headers
import com.squareup.okhttp.RequestBody

/**
 * An interface that represents something that works like a network stack, be it a real stack or a mock stack.
 * Created by jivie on 1/13/16.
 */
interface NetStack {

    /**
     * Synchronously makes an HTTP GET request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     */
    public fun syncGet(headers: Headers, url: String): NetResponse

    /**
     * Synchronously makes an HTTP POST request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun syncPost(headers: Headers, url: String, body: RequestBody): NetResponse

    /**
     * Synchronously makes an HTTP PUT request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun syncPut(headers: Headers, url: String, body: RequestBody): NetResponse
    /**
     * Synchronously makes an HTTP PATCH request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun syncPatch(headers: Headers, url: String, body: RequestBody): NetResponse

    /**
     * Synchronously makes an HTTP DELETE request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun syncDelete(headers: Headers, url: String, body: RequestBody = RequestBody.create(null, ByteArray(0))): NetResponse


    //Shortcuts

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
    public fun get(url: String, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncGet(Networking.HEADERS_EMPTY, url) }, onResult)

    /**
     * Asynchronously makes an HTTP POST request with empty headers.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun post(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPost(Networking.HEADERS_EMPTY, url, body) }, onResult)

    /**
     * Asynchronously makes an HTTP PUT request with empty headers.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun put(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPut(Networking.HEADERS_EMPTY, url, body) }, onResult)

    /**
     * Asynchronously makes an HTTP PATCH request with empty headers.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun patch(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncPatch(Networking.HEADERS_EMPTY, url, body) }, onResult)

    /**
     * Asynchronously makes an HTTP DELETE request with empty headers.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun delete(url: String, body: RequestBody, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncDelete(Networking.HEADERS_EMPTY, url, body) }, onResult)

    /**
     * Asynchronously makes an HTTP DELETE request with empty headers.
     * @param url The URL the request is made to.
     */
    public fun delete(url: String, onResult: (NetResponse) -> Unit): Unit = doAsync({ syncDelete(Networking.HEADERS_EMPTY, url) }, onResult)
}