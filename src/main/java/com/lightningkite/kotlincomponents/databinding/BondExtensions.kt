package com.lightningkite.kotlincomponents.databinding

import android.content.Context
import android.text.InputType
import android.view.View
import android.widget.*
import com.lightningkite.kotlincomponents.adapter.AdaptableViewController
import com.lightningkite.kotlincomponents.adapter.ViewControllerAdapter
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
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

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the text here will be updated.
 */
public fun EditText.bindString(bond: Bond<String>) {
    setText(bond.get())
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->
            if (bond.get() != charSequence) {
                bond.set(charSequence.toString())
            }
        }
    }
    bond.bind {
        if (bond.get() != text.toString()) {
            this.setText(bond.get())
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the text here will be updated.
 */
public fun EditText.bindNullableString(bond: Bond<String?>) {
    setText(bond.get())
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->
            if (bond.get() != charSequence) {
                bond.set(charSequence.toString())
            }
        }
    }
    bond.bind {
        if (bond.get() != text.toString()) {
            this.setText(bond.get())
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the integer here will be updated.
 */
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

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the number here will be updated.
 */
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

/**
 * Binds this [Switch] two way to the bond.
 */
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

/**
 * Makes this [TextView] display the value of the bond.
 */
public fun TextView.bindString(bond: Bond<String>) {
    bond.bind {
        this.text = bond.get()
    }
}

/**
 * Makes this [TextView] display the value of the bond.
 */
public fun TextView.bindAny(bond: Bond<Any>) {
    bond.bind {
        this.text = bond.get().toString()
    }
}

/**
 * Binds this [Spinner] two way to the bond.
 * When the user picks a new value from the spinner, the value of the bond will change to the index of the new value.
 * When the value of the bond changes, the item will shown will be updated.
 */
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

/**
 * Binds this [RadioButton] two way to the bond.
 * When the user picks this radio button, [bond] is set to [value]
 * When the value of the bond changes, it will be shown as checked if they are equal.
 */
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

public fun <T> ListView.bindArray(activity: VCActivity, bond: Bond<Array<T>>, makeView: AdaptableViewController<T>.() -> View) {
    val thisAdapter = ViewControllerAdapter.quick(activity, ArrayList(), makeView)
    adapter = thisAdapter
    bond.bind {
        thisAdapter.list = it.toArrayList()
    }
}
public fun <T> ListView.bindList(activity: VCActivity, bond: Bond<in List<T>>, makeView: AdaptableViewController<T>.() -> View) {
    val thisAdapter = ViewControllerAdapter.quick(activity, ArrayList(), makeView)
    adapter = thisAdapter
    bond.bind { list ->
        thisAdapter.list = list
    }
}

public fun <T> ListView.bindNullableList(activity: VCActivity, bond: Bond<in List<T>?>, makeView: AdaptableViewController<T>.() -> View) {
    val thisAdapter = ViewControllerAdapter.quick(activity, ArrayList(), makeView)
    adapter = thisAdapter
    bond.bind { list ->
        if(list == null) {
            thisAdapter.list = ArrayList()
        } else {
            thisAdapter.list = list
        }
    }
}