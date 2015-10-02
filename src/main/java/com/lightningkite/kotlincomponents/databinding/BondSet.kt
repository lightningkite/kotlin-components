package com.lightningkite.kotlincomponents.databinding

import android.content.Context
import android.view.View
import com.lightningkite.kotlincomponents.viewcontroller.AutocleanViewController
import com.lightningkite.kotlincomponents.viewcontroller.ViewControllerStack
import java.util.*

/**
 * Created by jivie on 9/2/15.
 */
public class BondSet() : AutocleanViewController.Listener {

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

    public fun <T> add(bond: Bond<T>): Bond<T> {
        list.add(bond)
        return bond
    }

    public fun clearBindings() {
        for (bond in list) {
            bond.clearBindings()
        }
    }

    override fun make(context: Context, stack: ViewControllerStack) {
        for (bond in list) {
            bond.make(context, stack)
        }
    }

    override fun unmake(view: View) {
        for (bond in list) {
            bond.unmake(view)
        }
    }

    override fun dispose() {
        for (bond in list) {
            bond.dispose()
        }
    }

}