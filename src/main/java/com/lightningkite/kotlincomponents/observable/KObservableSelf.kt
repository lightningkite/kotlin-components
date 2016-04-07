package com.lightningkite.kotlincomponents.observable

/**
 * Created by jivie on 4/7/16.
 */
abstract class KObservableSelf<T> : KObservableBase<T>() {
    override fun get(): T {
        return this as T
    }

    override fun set(v: T) {
        throw UnsupportedOperationException()
    }

    override fun update() {
        update(this as T)
    }
}