package com.lightningkite.kotlincomponents.databinding

import android.widget.EditText
import org.jetbrains.anko.*

/**
 * Created by jivie on 6/25/15.
 */

public fun EditText.bindString(bond: Bond<String>) {
    text = bond.get()
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->
            if (!bond.get().equals(charSequence)) {
                bond.set(charSequence.toString())
            }
        }
    }
    bond.bind {
        if (!bond.get().equals(getText().toString())) {
            this.setText(bond.get())
        }
    }
}

public fun EditText.bindInt(bond: Bond<Int>) {
    text = bond.get().toString()
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->
            if (!bond.get().toString().equals(charSequence)) {
                bond.set(java.lang.Integer.parseInt(charSequence.toString()))
            }
        }
    }
    bond.bind {
        if (!bond.get().toString().equals(getText().toString())) {
            this.setText(bond.get())
        }
    }
}