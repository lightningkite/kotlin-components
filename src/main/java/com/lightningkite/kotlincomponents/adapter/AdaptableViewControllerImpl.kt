package com.lightningkite.kotlincomponents.adapter

import com.lightningkite.kotlincomponents.databinding.Bond

/**
 * Created by jivie on 9/2/15.
 */
public abstract class AdaptableViewControllerImpl<T>(initial: T) : AdaptableViewController<T> {
    override var itemBond: Bond<T> = Bond(initial)
}
