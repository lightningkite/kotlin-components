package com.lightningkite.kotlincomponents.observable

import com.lightningkite.kotlincomponents.Disposable

/**
 * Created by jivie on 4/5/16.
 */
class KObservableObservable<T>(initialObservable: KObservableInterface<T>) : KObservableBuffered<T>(), Disposable {
    val myListener: (T) -> Unit = {
        super.update()
    }


    init {
        initialObservable.add(myListener)
    }

    var observable: KObservableInterface<T> = initialObservable
        set(value) {
            field.remove(myListener)
            field = value
            field.add(myListener)
            super.update()
        }

    override fun dispose() {
        observable.remove(myListener)
    }


    override fun getter(): T {
        return observable.get()
    }

    override fun setter(value: T) {
        observable.set(value)
    }

    override fun update() {
        observable.update() //"Why?" you may ask.  It's because this allows the KSyncedList to store the change.
        super.update()
    }
}