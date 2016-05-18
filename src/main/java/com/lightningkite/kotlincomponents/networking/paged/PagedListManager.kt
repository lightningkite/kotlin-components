package com.lightningkite.kotlincomponents.networking.paged

import com.lightningkite.kotlincomponents.networking.NetResponse
import com.lightningkite.kotlincomponents.observable.KObservableInterface
import com.lightningkite.kotlincomponents.observable.KObservableListInterface

/**
 * Created by jivie on 5/18/16.
 */
interface PagedListManager<T>: KObservableListInterface<T>{
    val currentPageObs:KObservableInterface<Int>
    val canPullObs: KObservableInterface<Boolean>
    val loadingObs:KObservableInterface<Boolean>

    fun pullInternal(onResult:(message:String, error: NetResponse?)->Unit)
    fun pull(onResult:(message:String, error: NetResponse?)->Unit = {a, b ->}){
        if(loadingObs.get()) return
        if(!canPullObs.get()) return
        pullInternal(onResult)
    }
    fun reset()
}