package com.lightningkite.kotlincomponents.adapter

import android.view.View
import com.lightningkite.kotlincomponents.databinding.Bond
import com.lightningkite.kotlincomponents.viewcontroller.ViewController

/**
 * Created by jivie on 9/2/15.
 */
public interface AdaptableViewController<T> : ViewController {
    public var itemBond: Bond<T>
    public var item: T
        get() = itemBond.get()
        set(value) = itemBond.set(value)

    override fun dispose(view: View) {
        itemBond.clearBindings()
    }
}