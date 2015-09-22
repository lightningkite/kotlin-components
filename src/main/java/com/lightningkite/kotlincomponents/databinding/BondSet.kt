package com.lightningkite.kotlincomponents.databinding

import java.util.*

/**
 * Created by jivie on 9/2/15.
 */
public class BondSet() {
    public val list: ArrayList<Bond<*>> = ArrayList()

    public inline fun <reified T : Any?> make(initialValue: T): Bond<T> {
        val newBond = Bond(initialValue)
        list.add(newBond)
        return newBond
    }

    public inline fun <reified T : Any?> makePermanent(initialValue: T): PermanentBond<T> {
        val newBond = PermanentBond(initialValue)
        list.add(newBond)
        return newBond
    }

    public fun dispose() {
        for (bond in list) {
            bond.clearBindings()
        }
    }

}