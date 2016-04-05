package com.lightningkite.kotlincomponents.observable

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1

/**
 * Created by jivie on 2/22/16.
 */
class KObservableReference<T>(val getterFun: () -> T, val setterFun: (T) -> Unit) : KObservableBuffered<T>() {
    override fun setter(value: T) = setterFun(value)

    override fun getter(): T = getterFun()

}

fun <T> KMutableProperty0<T>.toKObservableReference(): KObservableReference<T> {
    return KObservableReference({ get() }, { set(it) })
}

fun <T, R> KMutableProperty1<R, T>.toKObservableReference(receiver: R): KObservableReference<T> {
    return KObservableReference({ get(receiver) }, { set(receiver, it) })
}