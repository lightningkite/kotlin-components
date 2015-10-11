package com.lightningkite.kotlincomponents.databinding

import android.content.Context
import android.text.InputType
import android.view.View
import android.widget.*
import com.lightningkite.kotlincomponents.adapter.AdaptableViewController
import com.lightningkite.kotlincomponents.adapter.ViewControllerAdapter
import com.lightningkite.kotlincomponents.viewcontroller.ViewControllerStack
import org.jetbrains.anko.onCheckedChange
import org.jetbrains.anko.opaque
import org.jetbrains.anko.textChangedListener
import org.jetbrains.anko.textColor
import java.text.NumberFormat
import java.util.*

/**
 * Various extension functions to support bonds.
 * Created by jivie on 7/22/15.
 */


public fun EditText.bindString(bond: Bond<String>) {
    setText(bond.get())
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->
            if (!bond.get().equals(charSequence)) {
                bond.set(charSequence.toString())
            }
        }
    }
    bond.bind {
        if (!bond.get().equals(text.toString())) {
            this.setText(bond.get())
        }
    }
}

public fun EditText.bindInt(bond: Bond<Int>) {
    inputType = (inputType and 0xFFFFFFF0.toInt()) or InputType.TYPE_CLASS_NUMBER
    setText(bond.get().toString())
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->
            if (!bond.get().toString().equals(charSequence)) {
                bond.set(charSequence.toString().toInt())
            }
        }
    }
    bond.bind {
        if (!bond.get().toString().equals(text.toString())) {
            this.setText(bond.get())
        }
    }
}

public fun EditText.bindFloat(bond: Bond<Float>, format: NumberFormat) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
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
        val value = text.toString().toFloat()
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
        if (isChecked != bond.get()) {
            isChecked = bond.get();
        }
    }
}

public fun Switch.bindArray(bond: Bond<Array<Boolean>>, index: Int) {
    this.onCheckedChange {
        buttonView: android.widget.CompoundButton?, isChecked: Boolean ->
        Unit
        if (isChecked != bond.get()[index]) {
            bond.get()[index] = isChecked
            bond.update()
        }
    }
    bond.bind {
        val value = bond.get()[index]
        if (isChecked != value) {
            isChecked = value;
        }
    }
}

public fun CheckBox.bindArray(bond: Bond<Array<Boolean>>, index: Int) {
    this.onCheckedChange {
        buttonView: android.widget.CompoundButton?, isChecked: Boolean ->
        Unit
        if (isChecked != bond.get()[index]) {
            bond.get()[index] = isChecked
            bond.update()
        }
    }
    bond.bind {
        val value = bond.get()[index]
        if (isChecked != value) {
            isChecked = value;
        }
    }
}

public fun TextView.bindString(bond: Bond<String>) {
    bond.bind {
        this.text = bond.get()
    }
}

public fun TextView.bindAny(bond: Bond<Any>) {
    bond.bind {
        this.text = bond.get().toString()
    }
}

public fun Spinner.bindIndex(bond: Bond<Int>) {
    bond.bind {
        if (selectedItemPosition != it) {
            setSelection(it)
        }
    }
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position != bond.get()) {
                bond.set(position)
            }
        }

    }
}

public fun <T> RadioButton.bind(bond: Bond<T>, value: T) {
    bond.bind {
        isChecked = value == bond.get()
    }
    onCheckedChange { compoundButton, checked ->
        if (checked && bond.get() != value) {
            bond.set(value)
        }
    }
}

public fun <T> ListView.bind(context: Context, bond: Bond<Array<T>>, makeView: AdaptableViewController<T>.() -> View) {
    val thisAdapter = ViewControllerAdapter.quick(context, ViewControllerStack.dummy, ArrayList(), makeView)
    adapter = thisAdapter
    bond.bind {
        thisAdapter.list = it.toArrayList()
    }
}