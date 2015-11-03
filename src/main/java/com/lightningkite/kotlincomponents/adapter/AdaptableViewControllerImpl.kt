package com.lightningkite.kotlincomponents.adapter

import com.lightningkite.kotlincomponents.databinding.Bond

/**
 * An [AdaptableViewController] implemented.  Can and should be used when your adaptable view controller can extend it.
 * Created by jivie on 9/2/15.
 */
public abstract class AdaptableViewControllerImpl<T>(initial: T) : AdaptableViewController<T> {
    override var itemBond: Bond<T> = Bond(initial)
    override var index:Int = 0
}
