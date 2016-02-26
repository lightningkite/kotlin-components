package com.lightningkite.kotlincomponents.networking

import android.util.Log
import com.github.salomonbrys.kotson.typeToken
import com.lightningkite.kotlincomponents.gsonFrom
import com.lightningkite.kotlincomponents.observable.KObservable
import com.lightningkite.kotlincomponents.observable.KObservableList
import com.lightningkite.kotlincomponents.observable.KObservableListInterface
import com.lightningkite.kotlincomponents.runAll
import java.lang.reflect.Type
import java.util.*

/**
 * Created by jivie on 2/26/16.
 */
inline fun <reified T : Any> PagedEndpoint(
        url: String,
        listKey: String = "results"
): PagedEndpoint<T> = PagedEndpoint(typeToken<T>(), url, listKey)

class PagedEndpoint<T : Any>(
        val type: Type,
        val url: String,
        val listKey: String = "results",
        val list: KObservableList<T> = KObservableList(ArrayList())
) : KObservableListInterface<T> by list {

    companion object {
        val PAGE_REGEX = "page=([0-9]+)".toRegex()
        val PAGE_REPL = "page=$1"
    }

    init {
        pull()
    }

    val pullingObservable = KObservable(false)
    var pulling by pullingObservable

    var nextUrl: String? = url

    val onError = ArrayList<(NetResponse) -> Unit>()

    val isMoreObservable = KObservable(true)

    fun pull() {
        if (pulling) return
        if (nextUrl == null) return
        pulling = true
        var url = nextUrl!!

        Networking.get(url) {
            if (it.isSuccessful) {
                nextUrl = null
                val result = it.jsonObject()

                if (result.has("next")) {
                    nextUrl = result.get("next").asString
                }
                if (result.has("num_pages")) {
                    val pageResult = PAGE_REGEX.find(url)
                    if (pageResult != null) {
                        val num = pageResult.groupValues[1].toInt() + 1
                        if (num <= result.get("num_pages").asInt) {
                            nextUrl = PAGE_REGEX.replace(url) { "page=$num" }
                        }
                    }
                }

                list.addAll(result.getAsJsonArray(listKey).map { it.gsonFrom<T>(type)!! })
                if (nextUrl == null) {
                    isMoreObservable.set(false)
                }
            } else {
                Log.e("PagedEndpoint", "Couldn't get the page. $it")
                onError.runAll(it)
            }
            pulling = false
        }
    }
}