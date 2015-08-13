package com.lightningkite.kotlincomponents.databinding

import android.os.Bundle
import com.lightningkite.kotlincomponents.parcel.Bundler
import java.util.ArrayList

/**
 * Created by jivie on 6/25/15.
 */


public class Bond<T>(init: T) {
    private var listeners: ArrayList<(v: T) -> Unit> = ArrayList()
    private var myValue: T = init

    public fun get(thisRef: Any?, prop: PropertyMetadata): T {
        return myValue!!
    }

    public fun get(): T {
        return myValue!!
    }

    public fun set(thisRef: Any?, prop: PropertyMetadata, v: T) {
        myValue = v
        for (listener in listeners) {
            listener(v)
        }
    }

    public fun set(v: T) {
        myValue = v
        for (listener in listeners) {
            listener(v)
        }
    }

    public fun update() {
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

    public fun writeToBundle(bundle: Bundle, key: String) {
        Bundler.writeToBundle(bundle, key, myValue)
    }

    public fun loadFromBundle(bundle: Bundle, key: String) {
        myValue = Bundler.readFromBundle(bundle, key, myValue.javaClass, myValue) as T
    }
}