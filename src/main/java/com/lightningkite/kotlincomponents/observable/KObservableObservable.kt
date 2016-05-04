package com.lightningkite.kotlincomponents.observable

import com.lightningkite.kotlincomponents.Disposable

/**
 * Created by jivie on 4/5/16.
 */
class KObservableObservable<T>(initialObservable: KObservableInterface<T>) : KObservableBase<T>(), Disposable {
    val myListener: (T) -> Unit = {
        super.update(it)
    }


    init {
        initialObservable.add(myListener)
    }

    var observable: KObservableInterface<T> = initialObservable
        set(value) {
            field.remove(myListener)
            field = value
            field.add(myListener)
            super.update(value.get())
        }

    override fun dispose() {
        observable.remove(myListener)
    }

    override fun get(): T {
        return observable.get()
    }

    override fun set(v: T) {
        observable.set(v)
        update(v)
    }

    override fun update() {
        observable.update() //"Why?" you may ask.  It's because this allows the KSyncedList to store the change.
        super.update(get())
    }
}