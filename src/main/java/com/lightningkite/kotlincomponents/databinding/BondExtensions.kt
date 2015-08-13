package com.lightningkite.kotlincomponents.databinding

import android.text.InputType
import android.view.View
import android.widget.*
import org.jetbrains.anko.*
import java.text.NumberFormat

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
    inputType = (inputType and 0xFFFFFFF0.toInt()) or InputType.TYPE_CLASS_NUMBER
    text = bond.get().toString()
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->
            if (!bond.get().toString().equals(charSequence)) {
                bond.set(charSequence.toString().toInt())
            }
        }
    }
    bond.bind {
        if (!bond.get().toString().equals(getText().toString())) {
            this.setText(bond.get())
        }
    }
}

public fun EditText.bindFloat(bond: Bond<Float>, format: NumberFormat) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    text = bond.get().toString()
    val originalTextColor = this.getTextColors().getDefaultColor()
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->
            try {
                val value = charSequence.toString().toFloat()
                textColor = originalTextColor
                if (bond.get() != value) {
                    bond.set(charSequence.toString().toFloat())
                }
            } catch(e: NumberFormatException) {
                //do nothing.
                textColor = 0xFF0000.opaque
            }
        }
    }
    bond.bind {
        val value = getText().toString().toFloat()
        if (bond.get() != value) {
            this.setText(format.format(bond.get()))
        }
    }
}

public fun Switch.bind(bond: Bond<Boolean>) {
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

public fun Spinner.bindIndex(bond: Bond<Int>) {
    bond.bind {
        if (getSelectedItemPosition() != it) {
            setSelection(it)
        }
    }
    setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position != bond.get()) {
                bond.set(position)
            }
        }

    })
}