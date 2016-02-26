package com.lightningkite.kotlincomponents.networking

import com.lightningkite.kotlincomponents.runAll

/**
 * Created by jivie on 2/26/16.
 */
open class NetEndpoint(val netInterface: NetInterface, val preQueryUrl: String, val queryParams: List<String> = listOf()) {
    val url: String = if (queryParams.isEmpty()) preQueryUrl else preQueryUrl + "?" + queryParams.joinToString("&")

    fun sub(subUrl: String) = NetEndpoint(netInterface, preQueryUrl + subUrl, queryParams)
    fun sub(id: Long) = NetEndpoint(netInterface, preQueryUrl + id.toString(), queryParams)
    fun query(key: String) = NetEndpoint(netInterface, preQueryUrl, queryParams + key)
    fun query(key: String, value: String) = NetEndpoint(netInterface, preQueryUrl, queryParams + (key + "=" + value))


    inline fun <reified T : Any> paged(listKey: String = "results") = PagedEndpoint<T>(url)

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

    //-----------GENERATE-------------

    inline fun <reified T : Any> generate(method: NetMethod)
            = { data: Any?, onResult: (T) -> Unit ->
        request(method, data, onResult = onResult)
    }

    inline fun <reified T : Any> generate(method: NetMethod, crossinline converter: () -> Any?)
            = { onResult: (T) -> Unit ->
        request(method, converter(), onResult = onResult)
    }

    inline fun <reified T : Any, A> generate(method: NetMethod, crossinline converter: (A) -> Any?)
            = { a: A, onResult: (T) -> Unit ->
        request(method, converter(a), onResult = onResult)
    }

    inline fun <reified T : Any, A, B> generate(method: NetMethod, crossinline converter: (A, B) -> Any?)
            = { a: A, b: B, onResult: (T) -> Unit ->
        request(method, converter(a, b), onResult = onResult)
    }

    inline fun <reified T : Any, A, B, C> generate(method: NetMethod, crossinline converter: (A, B, C) -> Any?)
            = { a: A, b: B, c: C, onResult: (T) -> Unit ->
        request(method, converter(a, b, c), onResult = onResult)
    }



    //------------ASYNC---------------

    inline fun <reified T : Any> request(
            method: NetMethod,
            data: Any?,
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
            crossinline onResult: (T) -> Unit
    ) = request(method, data, specialHeaders, { true }, onResult)

    inline fun <reified T : Any> request(
            method: NetMethod,
            data: Any?,
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) {
        netInterface.stack.request(
                method,
                url,
                data?.gsonToNetBody() ?: NetBody.EMPTY,
                netInterface.defaultHeaders + specialHeaders
        ) {
            val result = dealWithResult<T>(it, onError)
            if (result != null) onResult(result)
        }
    }

    inline fun <reified T : Any> get(
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.GET, null, specialHeaders, { true }, onResult)

    inline fun <reified T : Any> get(
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.GET, null, specialHeaders, onError, onResult)

    inline fun <reified T : Any> post(
            data: Any?,
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.POST, data, specialHeaders, { true }, onResult)

    inline fun <reified T : Any> post(
            data: Any?,
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.POST, data, specialHeaders, onError, onResult)


    inline fun <reified T : Any> put(
            data: Any?,
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.PUT, data, specialHeaders, { true }, onResult)

    inline fun <reified T : Any> put(
            data: Any?,
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.PUT, data, specialHeaders, onError, onResult)


    inline fun <reified T : Any> patch(
            data: Any?,
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.PATCH, data, specialHeaders, { true }, onResult)

    inline fun <reified T : Any> patch(
            data: Any?,
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.PATCH, data, specialHeaders, onError, onResult)


    inline fun <reified T : Any> delete(
            data: Any?,
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.DELETE, data, specialHeaders, { true }, onResult)

    inline fun <reified T : Any> delete(
            data: Any?,
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = request(NetMethod.DELETE, data, specialHeaders, onError, onResult)

    //------------SYNC---------------

    inline fun <reified T : Any> syncRequest(method: NetMethod, data: Any?, specialHeaders: Map<String, String> = NetHeader.EMPTY): T? = syncGet(specialHeaders, { true })
    inline fun <reified T : Any> syncRequest(
            method: NetMethod,
            data: Any?,
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
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

    inline fun <reified T : Any> syncGet(specialHeaders: Map<String, String> = NetHeader.EMPTY): T?
            = syncRequest(NetMethod.GET, null, specialHeaders)

    inline fun <reified T : Any> syncGet(specialHeaders: Map<String, String> = NetHeader.EMPTY, onError: (NetResponse) -> Boolean): T?
            = syncRequest(NetMethod.GET, null, specialHeaders, onError)

    inline fun <reified T : Any> syncPost(data: Any?, specialHeaders: Map<String, String> = NetHeader.EMPTY): T?
            = syncRequest(NetMethod.POST, data, specialHeaders)

    inline fun <reified T : Any> syncPost(data: Any?, specialHeaders: Map<String, String> = NetHeader.EMPTY, onError: (NetResponse) -> Boolean): T?
            = syncRequest(NetMethod.POST, data, specialHeaders, onError)

    inline fun <reified T : Any> syncPut(data: Any?, specialHeaders: Map<String, String> = NetHeader.EMPTY): T?
            = syncRequest(NetMethod.PUT, data, specialHeaders)

    inline fun <reified T : Any> syncPut(data: Any?, specialHeaders: Map<String, String> = NetHeader.EMPTY, onError: (NetResponse) -> Boolean): T?
            = syncRequest(NetMethod.PUT, data, specialHeaders, onError)

    inline fun <reified T : Any> syncPatch(data: Any?, specialHeaders: Map<String, String> = NetHeader.EMPTY): T?
            = syncRequest(NetMethod.PATCH, data, specialHeaders)

    inline fun <reified T : Any> syncPatch(data: Any?, specialHeaders: Map<String, String> = NetHeader.EMPTY, onError: (NetResponse) -> Boolean): T?
            = syncRequest(NetMethod.PATCH, data, specialHeaders, onError)

    inline fun <reified T : Any> syncDelete(data: Any?, specialHeaders: Map<String, String> = NetHeader.EMPTY): T?
            = syncRequest(NetMethod.DELETE, data, specialHeaders)

    inline fun <reified T : Any> syncDelete(data: Any?, specialHeaders: Map<String, String> = NetHeader.EMPTY, onError: (NetResponse) -> Boolean): T?
            = syncRequest(NetMethod.DELETE, data, specialHeaders, onError)
}