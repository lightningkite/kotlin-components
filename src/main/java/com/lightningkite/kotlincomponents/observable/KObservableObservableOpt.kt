package com.lightningkite.kotlincomponents.observable

import com.lightningkite.kotlincomponents.Disposable

/**
 * Created by jivie on 4/5/16.
 */
class KObservableObservableOpt<T>(initialObservable: KObservableInterface<T>? = null) : KObservableBase<T?>(), Disposable {
    val myListener: (T) -> Unit = {
        super.update(it)
    }

    var observable: KObservableInterface<T>? = null
        set(value) {
            field?.remove(myListener)
            field = value
            field?.add(myListener)
            super.update(value?.get())
        }

    init {
        observable = initialObservable
    }

    override fun get(): T? {
        return observable?.get()
    }

    override fun set(v: T?) {
        observable?.set(v as T) //this will throw an error if you give it an invalid value
        update(v)
    }

    override fun update() {
        observable?.update() //"Why?" you may ask.  It's because this allows the KSyncedList to store the change.
        super.update(get())
    }

    override fun dispose() {
        observable?.remove(myListener)
    }

}