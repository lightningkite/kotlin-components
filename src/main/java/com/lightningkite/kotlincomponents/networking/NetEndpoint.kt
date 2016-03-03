package com.lightningkite.kotlincomponents.networking

import com.lightningkite.kotlincomponents.runAll
import java.util.*

/**
 * Created by jivie on 2/26/16.
 */
open class NetEndpoint(val netInterface: NetInterface, val preQueryUrl: String, val queryParams: Map<String, String> = mapOf()) {

    companion object {
        fun fromUrl(netInterface: NetInterface, url: String): NetEndpoint {
            val index = url.indexOf('?')
            return NetEndpoint(
                    netInterface,
                    url.substring(0, index),
                    url.substring(index + 1)
                            .split('&')
                            .map { it.split('=') }
                            .associateBy({ it[0] }, { it[1] })
            )
        }
    }

    fun fromUrl(url: String): NetEndpoint = fromUrl(netInterface, url)

    val url: String = if (queryParams.isEmpty()) preQueryUrl else preQueryUrl + "?" + queryParams.entries.joinToString("&") { it.key + "=" + it.value }

    fun sub(subUrl: String) = NetEndpoint(netInterface, preQueryUrl + "/" + subUrl, queryParams)
    fun sub(id: Long) = NetEndpoint(netInterface, preQueryUrl + "/" + id.toString(), queryParams)

    fun query(key: String, value: Any) = NetEndpoint(netInterface, preQueryUrl, queryParams + (key to value.toString()))
    fun queryOptional(key: String, value: Any?) = if (value != null) NetEndpoint(netInterface, preQueryUrl, queryParams + (key to value.toString())) else this


    inline fun <reified T : Any> paged(listKey: String = "results"): PagedEndpoint<T> = PagedEndpoint(this)
    inline fun <reified T : Any> paged(listKey: String = "results", noinline onError: (NetResponse) -> Boolean): PagedEndpoint<T> = PagedEndpoint(this, onError = onError)

    inline fun <reified T : Any> dealWithResult(response: NetResponse, onError: (NetResponse) -> Boolean): T? {
        if (response.isSuccessful) {
            if (T::class.java == Unit::class.java) return null
            val result = response.result<T>()
            if (result != null) return result
            else {
                if (onError(response)) {
                    netInterface.onError.runAll(response)
                }
            }
        } else {
            if (onError(response)) {
                netInterface.onError.runAll(response)
            }
        }
        return null
    }



    //------------ASYNC---------------

    //    inline fun <reified T : Any> request(
    //            method: NetMethod,
    //            data: Any?,
    //            specialHeaders: Map<String, String> = NetHeader.EMPTY,
    //            crossinline onResult: (T) -> Unit
    //    ) = request(method, data, specialHeaders, { true }, onResult)

    inline fun <reified T : Any> request(
            method: NetMethod,
            data: Any?,
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) {
        val headers = HashMap(netInterface.defaultHeaders)
        headers.plusAssign(specialHeaders)
        netInterface.stack.request(
                method,
                url,
                data?.gsonToNetBody() ?: NetBody.EMPTY,
                headers
        ) {
            println(it.string())
            val result = dealWithResult<T>(it, onError)
            if (result != null) onResult(result)
        }
    }

    inline fun <reified T : Any> get(
            specialHeaders: Map<String, String> = mapOf(),
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.GET, null, specialHeaders, onError, onResult)

    inline fun <reified T : Any> post(
            data: Any?,
            specialHeaders: Map<String, String> = mapOf(),
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.POST, data, specialHeaders, onError, onResult)

    inline fun <reified T : Any> put(
            data: Any?,
            specialHeaders: Map<String, String> = mapOf(),
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.PUT, data, specialHeaders, onError, onResult)

    inline fun <reified T : Any> patch(
            data: Any?,
            specialHeaders: Map<String, String> = mapOf(),
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.PATCH, data, specialHeaders, onError, onResult)

    inline fun <reified T : Any> delete(
            data: Any? = null,
            specialHeaders: Map<String, String> = mapOf(),
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.DELETE, data, specialHeaders, onError, onResult)

    //------------SYNC---------------

    inline fun <reified T : Any> syncRequest(method: NetMethod, data: Any?, specialHeaders: Map<String, String> = mapOf()): T? = syncGet(specialHeaders, { true })
    inline fun <reified T : Any> syncRequest(
            method: NetMethod,
            data: Any?,
            specialHeaders: Map<String, String> = mapOf(),
            onError: (NetResponse) -> Boolean
    ): T? = dealWithResult(
            netInterface.stack.sync(
                    method,
                    url,
                    data?.gsonToNetBody() ?: NetBody.EMPTY,
                    netInterface.defaultHeaders + specialHeaders
            ),
            onError
    )

    inline fun <reified T : Any> syncGet(specialHeaders: Map<String, String> = mapOf()): T?
            = syncRequest(NetMethod.GET, null, specialHeaders)

    inline fun <reified T : Any> syncGet(specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
            = syncRequest(NetMethod.GET, null, specialHeaders, onError)

    inline fun <reified T : Any> syncPost(data: Any?, specialHeaders: Map<String, String> = mapOf()): T?
            = syncRequest(NetMethod.POST, data, specialHeaders)

    inline fun <reified T : Any> syncPost(data: Any?, specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
            = syncRequest(NetMethod.POST, data, specialHeaders, onError)

    inline fun <reified T : Any> syncPut(data: Any?, specialHeaders: Map<String, String> = mapOf()): T?
            = syncRequest(NetMethod.PUT, data, specialHeaders)

    inline fun <reified T : Any> syncPut(data: Any?, specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
            = syncRequest(NetMethod.PUT, data, specialHeaders, onError)

    inline fun <reified T : Any> syncPatch(data: Any?, specialHeaders: Map<String, String> = mapOf()): T?
            = syncRequest(NetMethod.PATCH, data, specialHeaders)

    inline fun <reified T : Any> syncPatch(data: Any?, specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
            = syncRequest(NetMethod.PATCH, data, specialHeaders, onError)

    inline fun <reified T : Any> syncDelete(data: Any?, specialHeaders: Map<String, String> = mapOf()): T?
            = syncRequest(NetMethod.DELETE, data, specialHeaders)

    inline fun <reified T : Any> syncDelete(data: Any?, specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
            = syncRequest(NetMethod.DELETE, data, specialHeaders, onError)
}