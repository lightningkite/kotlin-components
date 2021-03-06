package com.lightningkite.kotlincomponents.networking

import com.github.salomonbrys.kotson.typeToken
import com.lightningkite.kotlincomponents.asStringOptional
import com.lightningkite.kotlincomponents.gsonFrom
import com.lightningkite.kotlincomponents.observable.KObservable
import com.lightningkite.kotlincomponents.observable.KObservableList
import com.lightningkite.kotlincomponents.observable.KObservableListInterface
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * Created by jivie on 2/26/16.
 */
inline fun <reified T : Any> PagedEndpoint(
        endpoint: NetEndpoint,
        listKey: String = "results"
): PagedEndpoint<T> = PagedEndpoint(typeToken<T>(), endpoint, listKey)

inline fun <reified T : Any> PagedEndpoint(
        endpoint: NetEndpoint,
        listKey: String = "results",
        noinline onError: (NetResponse) -> Boolean
): PagedEndpoint<T> = PagedEndpoint(typeToken<T>(), endpoint, listKey, onError)

open class PagedEndpoint<T : Any>(
        val type: Type,
        endpoint: NetEndpoint,
        val listKey: String = "results",
        val onError: (NetResponse) -> Boolean = { true },
        val isPaged: Boolean = true, //only exists for rapid prototyping
        val list: KObservableList<T> = KObservableList(ArrayList())
) : KObservableListInterface<T> by list {

    var endpoint: NetEndpoint = endpoint
        private set

    companion object {
        val PAGE_REGEX = "page=([0-9]+)".toRegex()
        val PAGE_REPL = "page=$1"
    }


    val pullingObservable = KObservable(false)
    var pulling by pullingObservable

    var nextEndpoint: NetEndpoint? = endpoint

    val isMoreObservable = KObservable(true)
    val firstLoadFinishedObservable = KObservable(false)


    val listType: ParameterizedType = object : ParameterizedType {
        override fun getRawType(): Type? = ArrayList::class.java
        override fun getOwnerType(): Type? = null
        override fun getActualTypeArguments(): Array<out Type>? = arrayOf(type)
    }

    init {
        pull()
    }

    fun reset(endpoint: NetEndpoint) {
        list.clear()
        pulling = false
        isMoreObservable.set(true)
        firstLoadFinishedObservable.set(false)
        nextEndpoint = endpoint
        pull()
    }

    fun pull() {
        if (pulling) return
        if (nextEndpoint == null) return
        pulling = true
        val currentEndpoint = nextEndpoint!!

        if (isPaged) {
            currentEndpoint.async(NetMethod.GET) { response ->
                if (!response.isSuccessful) {
                    onError(response)
                    return@async
                }
                val result = response.jsonObject()
                if (currentEndpoint != nextEndpoint) {
                    pulling = false
                    return@async
                }
                nextEndpoint = null
                if (result.has("num_pages")) {
                    val pageNum = ((currentEndpoint.queryParams["page"]?.toInt()) ?: 1) + 1
                    if (pageNum > result.get("num_pages").asString.toInt())
                        nextEndpoint = currentEndpoint.query("page", pageNum)
                } else if (result.has("next")) {
                    val nextUrl = result.get("next").asStringOptional
                    if (nextUrl != null) {
                        nextEndpoint = currentEndpoint.fromUrl(nextUrl)
                    }
                }
                list.addAll(result.getAsJsonArray(listKey).map { it.gsonFrom<T>(type)!! })
                if (nextEndpoint == null) {
                    isMoreObservable.set(false)
                }

                firstLoadFinishedObservable.set(true)
                pulling = false
            }
        } else {
            //option only exists for rapid prototyping purposes.  Servers often add pagination late.
            currentEndpoint.async(NetMethod.GET) { response ->
                if (!response.isSuccessful) {
                    onError(response)
                    return@async
                }
                val result = response.jsonArray()
                list.addAll(result.map { it.gsonFrom<T>(type)!! })
                firstLoadFinishedObservable.set(true)
                nextEndpoint = null
                pulling = false
            }
        }
    }
}