package com.lightningkite.kotlincomponents.databinding

import android.widget.TextView
import org.jetbrains.anko.*

/**
 * Created by jivie on 6/25/15.
 */
public fun TextView.bindString(bond: Bond<String>) {
    bond.bind {
        this.setText(bond.get())
    }
}