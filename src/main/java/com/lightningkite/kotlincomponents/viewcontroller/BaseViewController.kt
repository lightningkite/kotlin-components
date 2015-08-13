package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Context
import android.view.View
import com.lightningkite.kotlincomponents.databinding.Bond
import com.lightningkite.kotlincomponents.databinding.PermanentBond
import org.jetbrains.anko.onClick
import java.util.ArrayList

/**
 * Created by jivie on 6/26/15.
 */
public abstract class BaseViewController : ViewController {

    public var view: View? = null
    public var context: Context? = null
    public var stack: ViewControllerStack? = null
    public val disposeFunctions: ArrayList<() -> Unit> = ArrayList()
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
        disposeFunctions.add {
            newBond.clearBindings()
        }
        return newBond
    }

    protected inline fun <reified T> makePermanentBond(initialValue: T): PermanentBond<T> {
        val newBond = PermanentBond(initialValue)
        disposeFunctions.add {
            newBond.clearBindings()
        }
        return newBond
    }

    override fun dispose(view: View) {
        for (func in disposeFunctions) {
            func()
        }
        disposeFunctions.clear()
        this.context = null
        this.view = null
    }

}