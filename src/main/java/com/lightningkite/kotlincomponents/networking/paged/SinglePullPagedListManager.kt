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
 * For endpoints that aren't actually paged.  Pulls once and calls it good.
 * Created by jivie on 5/18/16.
 */
class SinglePullPagedListManager<T>(
        val type: Type,
        val endpoint: NetEndpoint,
        val list: KObservableListInterface<T> = KObservableList()
) : PagedListManager<T>, KObservableListInterface<T> by list {

    override val currentPageObs: KObservableInterface<Int> = KObservable(1)
    override val canPullObs: KObservableInterface<Boolean> = KObservable(true)
    override val loadingObs: KObservableInterface<Boolean> = KObservable(false)

    val listType: ParameterizedType = object : ParameterizedType {
        override fun getRawType(): Type? = ArrayList::class.java
        override fun getOwnerType(): Type? = null
        override fun getActualTypeArguments(): Array<out Type>? = arrayOf(type)
    }

    override fun pullInternal(onResult: (message: String, error: NetResponse?) -> Unit) {
        loadingObs.set(true)
        endpoint.async(NetMethod.GET) { response ->
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
            list.addAll(newItems)
            canPullObs.set(false)
        }
    }

    override fun reset() {
        list.clear()
        canPullObs.set(true)
        loadingObs.set(false)
        currentPageObs.set(1)
    }

}

inline fun <reified T : Any> SinglePullPagedListManager(
        endpoint: NetEndpoint,
        list: KObservableListInterface<T> = KObservableList()
) = SinglePullPagedListManager(typeToken<T>(), endpoint, list)