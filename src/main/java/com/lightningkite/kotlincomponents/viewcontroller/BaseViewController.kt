package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Context
import android.view.View
import com.lightningkite.kotlincomponents.databinding.Bond
import org.jetbrains.anko.onClick
import java.util.ArrayList

/**
 * Created by jivie on 6/26/15.
 */
public abstract class BaseViewController : ViewController {

    public var view: View? = null
    public var context: Context? = null
    public var stack: ViewControllerStack? = null
    public val bonds: ArrayList<Bond<*>> = ArrayList()
    override var result: Any? = null

    override fun make(context: Context, stack: ViewControllerStack): View {
        this.context = context
        this.stack = stack
        val newView = make()
        if (!newView.hasOnClickListeners()) newView.onClick {}
        view = newView
        return newView
    }

    public abstract fun make(): View

    protected inline fun <reified T> makeBond(initialValue: T): Bond<T> {
        val newBond = Bond(initialValue)
        bonds.add(newBond)
        return newBond
    }

    override fun dispose(view: View) {
        for (bond in bonds) {
            bond.clearBindings()
        }
        bonds.clear()
        this.context = null
        this.view = null
    }

}