package com.lightningkite.kotlincomponents.databinding

import android.widget.Switch
import org.jetbrains.anko.*

/**
 * Created by jivie on 6/25/15.
 */

public fun Switch.bindString(bond: Bond<Boolean>) {
    this.onCheckedChange {
        buttonView: android.widget.CompoundButton?, isChecked: Boolean ->
        Unit
        if (isChecked != bond.get()) {
            bond.set(isChecked);
        }
    }
    bond.bind {
        if (this.checked != bond.get()) {
            checked = bond.get();
        }
    }
}