package com.lightningkite.kotlincomponents.observable

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1

/**
 * Created by jivie on 2/22/16.
 */
class KObservableReference<T>(val getterFun: () -> T, val setterFun: (T) -> Unit) : KObservableBase<T>() {
    override fun update() {
        super.update(getterFun())
    }

    override fun get(): T = getterFun()

    override fun set(v: T) {
        setterFun(v)
        update()
    }

}

fun <T> KMutableProperty0<T>.toKObservableReference(): KObservableReference<T> {
    return KObservableReference({ get() }, { set(it) })
}

fun <T, R> KMutableProperty1<R, T>.toKObservableReference(receiver: R): KObservableReference<T> {
    return KObservableReference({ get(receiver) }, { set(receiver, it) })
}