package com.lightningkite.kotlincomponents.networking.paged

import com.github.salomonbrys.kotson.typeToken
import com.lightningkite.kotlincomponents.networking.NetEndpoint
import com.lightningkite.kotlincomponents.networking.NetMethod
import com.lightningkite.kotlincomponents.networking.NetResponse
import com.lightningkite.kotlincomponents.observable.KObservable
import com.lightningkite.kotlincomponents.observable.KObservableInterface
import com.lightningkite.kotlincomponents.observable.KObservableList
import com.lightningkite.kotlincomponents.observable.KObservableListInterface
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * Works with paged lists that use a parameter for determining the page and return data in a raw
 * JSON array, returning an empty array when it goes past the available results.
 * Created by jivie on 5/18/16.
 */
class RawPagedListManager<T>(
        val type: Type,
        val endpoint: NetEndpoint,
        val pageParamName: String = "page",
        val startIndex: Int = 1,
        val list: KObservableListInterface<T> = KObservableList()
) : PagedListManager<T>, KObservableListInterface<T> by list {

    override val currentPageObs: KObservableInterface<Int> = KObservable(startIndex)
    override val canPullObs: KObservableInterface<Boolean> = KObservable(true)
    override val loadingObs: KObservableInterface<Boolean> = KObservable(false)

    var numResets = 0

    val listType: ParameterizedType = object : ParameterizedType {
        override fun getRawType(): Type? = ArrayList::class.java
        override fun getOwnerType(): Type? = null
        override fun getActualTypeArguments(): Array<out Type>? = arrayOf(type)
    }

    override fun pullInternal(onResult: (message: String, error: NetResponse?) -> Unit) {

        loadingObs.set(true)
        val currentReset = numResets
        endpoint.query(pageParamName, currentPageObs.get()).async(NetMethod.GET) { response ->

            if (currentReset != numResets) return@async //ignores if this call is after a reset

            loadingObs.set(false)

            if (!response.isSuccessful) {
                onResult("Network call returned an error", response)
                return@async
            }
            val newItems = response.gson<ArrayList<T>>(listType)
            if (newItems == null) {
                onResult("Parsing error", response)
                return@async
            }
            if (newItems.isEmpty()) {
                canPullObs.set(false)
                return@async
            }
            list.addAll(newItems)
            currentPageObs.set(currentPageObs.get() + 1)
        }
    }

    override fun reset() {
        numResets++
        list.clear()
        canPullObs.set(true)
        loadingObs.set(false)
        currentPageObs.set(startIndex)
    }

}

inline fun <reified T : Any> RawPagedListManager(
        endpoint: NetEndpoint,
        pageParamName: String = "page",
        startIndex: Int = 1,
        list: KObservableListInterface<T> = KObservableList()
) = RawPagedListManager(typeToken<T>(), endpoint, pageParamName, startIndex, list)