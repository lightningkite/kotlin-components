package com.lightningkite.kotlincomponents.adapter

import android.view.View
import com.lightningkite.kotlincomponents.databinding.Bond
import com.lightningkite.kotlincomponents.viewcontroller.ViewController

/**
 * A view controller that can be used within an adapter, specifically the [ViewControllerAdapter].
 * Created by jivie on 9/2/15.
 */
public interface AdaptableViewController<T> : ViewController {
    /**
     * This bond contains the item this view controller is supposed to be displaying.
     */
    public var itemBond: Bond<T>
    /**
     * The item this view controller is supposed to be displaying.
     */
    public var item: T
        get() = itemBond.get()
        set(value) = itemBond.set(value)

    /**
     * The index of the item this view controller is supposed to be displaying.
     */
    public var index:Int
}