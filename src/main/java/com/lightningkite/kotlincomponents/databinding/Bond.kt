package com.lightningkite.kotlincomponents.databinding

import java.util.*

/**
 * Created by jivie on 6/25/15.
 */

public open class Bond<T : Any?>(init: T) {
    protected var listeners: ArrayList<(v: T) -> Unit> = ArrayList()
    protected var myValue: T = init

    public fun get(thisRef: Any?, prop: PropertyMetadata): T {
        return myValue
    }

    public fun get(): T {
        return myValue
    }

    public fun set(thisRef: Any?, prop: PropertyMetadata, v: T) {
        myValue = v
        update()
    }

    public fun set(v: T) {
        myValue = v
        update()
    }

    public open fun update() {
        for (listener in listeners) {
            listener(myValue)
        }
    }

    public fun bind(body: (v: T) -> Unit) {
        listeners.add(body)
        body(myValue)
    }

    public fun unbind(body: (v: T) -> Unit) {
        listeners.remove(body)
    }

    public fun clearBindings() {
        listeners.clear()
    }

    override fun toString(): String {
        return "Bond(" + myValue.toString() + ")"
    }
}