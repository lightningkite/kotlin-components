package com.lightningkite.kotlincomponents.databinding

import java.util.*

/**
 * A bond that can have permanent listeners.
 * Created by jivie on 6/25/15.
 */

public class PermanentBond<T : Any?>(init: T) : Bond<T>(init) {
    private var permanentListeners: ArrayList<(v: T) -> Unit> = ArrayList()

    override fun update() {
        super.update()
        for (listener in permanentListeners) {
            listener(myValue)
        }
    }

    public fun bindPermanent(body: (v: T) -> Unit) {
        permanentListeners.add(body)
        body(myValue)
    }

    public fun unbindPermanent(body: (v: T) -> Unit) {
        permanentListeners.remove(body)
    }
}