package com.lightningkite.kotlincomponents.networking

import com.lightningkite.kotlincomponents.async.doAsync

/**
 * An interface that represents something that works like a network stack, be it a real stack or a mock stack.
 * Created by jivie on 1/13/16.
 */
interface NetStack {

    /**
     * Synchronously makes a request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     */
    fun sync(method: NetMethod, url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY): NetResponse

    /**
     * Synchronously makes a GET request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     */
    public fun syncGet(url: String, headers: Map<String, String> = NetHeader.EMPTY): NetResponse {
        return sync(NetMethod.GET, url, NetBody.EMPTY, headers)
    }

    /**
     * Synchronously makes a POST request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun syncPost(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY): NetResponse {
        return sync(NetMethod.POST, url, body, headers)
    }

    /**
     * Synchronously makes a PUT request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun syncPut(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY): NetResponse {
        return sync(NetMethod.PUT, url, body, headers)
    }

    /**
     * Synchronously makes a PATCH request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun syncPatch(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY): NetResponse {
        return sync(NetMethod.PATCH, url, body, headers)
    }

    /**
     * Synchronously makes a DELETE request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun syncDelete(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY): NetResponse {
        return sync(NetMethod.DELETE, url, body, headers)
    }


    //Shortcuts

    /**
     * Asynchronously makes an HTTP GET request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     */
    public fun get(url: String, headers: Map<String, String> = NetHeader.EMPTY, onResult: (NetResponse) -> Unit): Unit {
        doAsync({ sync(NetMethod.GET, url, NetBody.EMPTY, headers) }, onResult)
    }

    /**
     * Asynchronously makes an HTTP POST request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun post(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY, onResult: (NetResponse) -> Unit): Unit {
        doAsync({ sync(NetMethod.POST, url, body, headers) }, onResult)
    }

    /**
     * Asynchronously makes an HTTP PUT request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun put(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY, onResult: (NetResponse) -> Unit): Unit {
        doAsync({ sync(NetMethod.PUT, url, body, headers) }, onResult)
    }

    /**
     * Asynchronously makes an HTTP PATCH request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun patch(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY, onResult: (NetResponse) -> Unit): Unit {
        doAsync({ sync(NetMethod.PATCH, url, body, headers) }, onResult)
    }

    /**
     * Asynchronously makes an HTTP DELETE request.
     * @param headers The headers used in this request.
     * @param url The URL the request is made to.
     * @param body The data to send in this request.
     */
    public fun delete(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY, onResult: (NetResponse) -> Unit): Unit {
        doAsync({ sync(NetMethod.DELETE, url, body, headers) }, onResult)
    }
}