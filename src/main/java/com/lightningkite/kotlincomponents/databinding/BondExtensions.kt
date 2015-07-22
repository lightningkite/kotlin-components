package com.lightningkite.kotlincomponents.databinding

import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import org.jetbrains.anko.checked
import org.jetbrains.anko.onCheckedChange
import org.jetbrains.anko.text
import org.jetbrains.anko.textChangedListener

/**
 * Various extension functions to support bonds.
 * Created by jivie on 7/22/15.
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

public fun TextView.bindString(bond: Bond<String>) {
    bond.bind {
        this.setText(bond.get())
    }
}