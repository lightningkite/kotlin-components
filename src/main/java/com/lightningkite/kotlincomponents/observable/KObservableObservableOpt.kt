package com.lightningkite.kotlincomponents.observable

import com.lightningkite.kotlincomponents.Disposable

/**
 * Created by jivie on 4/5/16.
 */
class KObservableObservableOpt<T>(initObservable: KObservableInterface<T>? = null) : KObservableBuffered<T?>(), Disposable {
    val myListener: (T?) -> Unit = {
        super.update()
    }

    var observable: KObservableInterface<T>? = null
        set(value) {
            field?.remove(myListener)
            field = value
            field?.add(myListener)
            super.update()
        }

    override fun dispose() {
        observable?.remove(myListener)
    }

    init {
        observable = initObservable
    }


    override fun getter(): T? {
        return observable?.get()
    }

    override fun setter(value: T?) {
        observable?.set(value as T) //this will throw an error if you give it an invalid value
    }

    override fun update() {
        observable?.update() //"Why?" you may ask.  It's because this allows the KSyncedList to store the change.
        super.update()
    }
}