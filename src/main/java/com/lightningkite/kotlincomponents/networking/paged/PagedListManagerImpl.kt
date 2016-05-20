package com.lightningkite.kotlincomponents.networking.paged

import com.lightningkite.kotlincomponents.observable.KObservable
import com.lightningkite.kotlincomponents.observable.KObservableInterface

/**
 * Created by jivie on 5/18/16.
 */
abstract class PagedListManagerImpl<T> : PagedListManager<T> {

    override val currentPageObs: KObservableInterface<Int> = KObservable(1)
    override val canPullObs: KObservableInterface<Boolean> = KObservable(true)
    override val loadingObs: KObservableInterface<Boolean> = KObservable(false)
}