package com.lightningkite.kotlincomponents.viewcontroller

import org.jetbrains.anko.dip

/**
 * Created by jivie on 7/31/15.
 */
public fun BaseViewController.dip(value: Int): Int {
    return context!!.dip(value)
}

public fun BaseViewController.dip(value: Float): Int {
    return context!!.dip(value)
}