package com.lightningkite.kotlincomponents.networking

import com.github.salomonbrys.kotson.typeToken
import com.google.gson.JsonObject
import com.lightningkite.kotlincomponents.gsonFrom
import com.lightningkite.kotlincomponents.observable.KObservable
import com.lightningkite.kotlincomponents.observable.KObservableList
import com.lightningkite.kotlincomponents.observable.KObservableListInterface
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

class PagedEndpoint<T : Any>(
        val type: Type,
        endpoint: NetEndpoint,
        val listKey: String = "results",
        val onError: (NetResponse) -> Boolean = { true },
        val list: KObservableList<T> = KObservableList(ArrayList())
) : KObservableListInterface<T> by list {

    var endpoint: NetEndpoint = endpoint
        private set

    companion object {
        val PAGE_REGEX = "page=([0-9]+)".toRegex()
        val PAGE_REPL = "page=$1"
    }

    init {
        pull()
    }

    val pullingObservable = KObservable(false)
    var pulling by pullingObservable

    var nextEndpoint: NetEndpoint? = endpoint

    val isMoreObservable = KObservable(true)

    fun reset(endpoint: NetEndpoint) = reset(endpoint.url)
    fun reset(newUrl: String) {
        isMoreObservable.set(true)
        nextEndpoint = endpoint
        pull()
    }

    fun pull() {
        if (pulling) return
        if (nextEndpoint == null) return
        pulling = true
        var currentEndpoint = nextEndpoint!!

        currentEndpoint.get<JsonObject>(onError = onError) { result ->
            nextEndpoint = null

            if (result.has("num_pages")) {
                val pageNum = ((currentEndpoint.queryParams["page"]?.toInt()) ?: 1) + 1
                if (pageNum > result.get("num_pages").asString.toInt())
                    nextEndpoint = currentEndpoint.query("page", pageNum)
            } else if (result.has("next")) {
                nextEndpoint = currentEndpoint.fromUrl(result.getAsJsonPrimitive("next").asString)
            }

            list.addAll(result.getAsJsonArray(listKey).map { it.gsonFrom<T>(type)!! })
            if (nextEndpoint == null) {
                isMoreObservable.set(false)
            }
            pulling = false
        }
    }
}