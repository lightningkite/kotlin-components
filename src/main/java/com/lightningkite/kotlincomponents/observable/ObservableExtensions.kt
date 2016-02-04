package com.lightningkite.kotlincomponents.observable

import android.text.InputType
import android.view.View
import android.widget.*
import com.lightningkite.kotlincomponents.adapter.LightningAdapter
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import org.jetbrains.anko.*
import java.text.NumberFormat
import java.util.*

/**
 * Various extension functions to support bonds.
 * Created by jivie on 7/22/15.
 */

fun <T> View.bind(observable: MutableCollection<T>, item: T) {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View?) {
            observable.remove(item)
            this@bind.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
            observable.add(item)
        }
    })
}

fun <A, B> View.bind(observableA: KObservableInterface<A>, observableB: KObservableInterface<B>, action: (A, B) -> Unit) {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {

        val itemA = { item: A -> action(item, observableB.get()) }
        val itemB = { item: B -> action(observableA.get(), item) }

        override fun onViewDetachedFromWindow(v: View?) {
            observableA.remove(itemA)
            observableB.remove(itemB)
            this@bind.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
            observableA.add(itemA)
            observableB.add(itemB)
        }
    })
}

fun <A, B, C> View.bind(
        observableA: KObservableInterface<A>,
        observableB: KObservableInterface<B>,
        observableC: KObservableInterface<C>,
        action: (A, B, C) -> Unit
) {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {

        val itemA = { item: A -> action(item, observableB.get(), observableC.get()) }
        val itemB = { item: B -> action(observableA.get(), item, observableC.get()) }
        val itemC = { item: C -> action(observableA.get(), observableB.get(), item) }

        override fun onViewDetachedFromWindow(v: View?) {
            observableA.remove(itemA)
            observableB.remove(itemB)
            observableC.remove(itemC)
            this@bind.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
            observableA.add(itemA)
            observableB.add(itemB)
            observableC.add(itemC)
        }
    })
}

fun <A, B> View.bind(observableA: KObservableInterface<A>, observableB: KObservableInterface<B>, action: () -> Unit) {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {

        val itemA = { item: A -> action() }
        val itemB = { item: B -> action() }

        override fun onViewDetachedFromWindow(v: View?) {
            observableA.remove(itemA)
            observableB.remove(itemB)
            this@bind.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
            observableA.add(itemA)
            observableB.add(itemB)
        }
    })
}

fun <A, B, C> View.bind(
        observableA: KObservableInterface<A>,
        observableB: KObservableInterface<B>,
        observableC: KObservableInterface<C>,
        action: () -> Unit
) {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {

        val itemA = { item: A -> action() }
        val itemB = { item: B -> action() }
        val itemC = { item: C -> action() }

        override fun onViewDetachedFromWindow(v: View?) {
            observableA.remove(itemA)
            observableB.remove(itemB)
            observableC.remove(itemC)
            this@bind.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
            observableA.add(itemA)
            observableB.add(itemB)
            observableC.add(itemC)
        }
    })
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the text here will be updated.
 */
inline fun EditText.bindString(bond: KObservableInterface<String>) {
    setText(bond.get())
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->
            if (bond.get() != charSequence) {
                bond.set(charSequence.toString())
            }
        }
    }
    bind(bond) {
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
inline fun EditText.bindNullableString(bond: KObservableInterface<String?>) {
    setText(bond.get())
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->
            if (bond.get() != charSequence) {
                bond.set(charSequence.toString())
            }
        }
    }
    bind(bond) {
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
inline fun EditText.bindInt(bond: KObservableInterface<Int>) {
    inputType = (inputType and 0xFFFFFFF0.toInt()) or InputType.TYPE_CLASS_NUMBER
    setText(bond.get().toString())
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->
            if (!bond.get().toString().equals(charSequence)) {
                bond.set(charSequence.toString().toInt())
            }
        }
    }
    bind(bond) {
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
inline fun EditText.bindFloat(bond: KObservableInterface<Float>, format: NumberFormat) {
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
    bind(bond) {
        val value = text.toString().toFloat()
        if (bond.get() != value) {
            this.setText(format.format(bond.get()))
        }
    }
}

/**
 * Binds this [Switch] two way to the bond.
 */
inline fun Switch.bind(bond: KObservableInterface<Boolean>) {
    this.onCheckedChange {
        buttonView: android.widget.CompoundButton?, isChecked: Boolean ->
        Unit
        if (isChecked != bond.get()) {
            bond.set(isChecked);
        }
    }
    bind(bond) {
        if (isChecked != bond.get()) {
            isChecked = bond.get();
        }
    }
}

inline fun Switch.bindArray(bond: KObservableInterface<Array<Boolean>>, index: Int) {
    this.onCheckedChange {
        buttonView: android.widget.CompoundButton?, isChecked: Boolean ->
        Unit
        if (isChecked != bond.get()[index]) {
            bond.get()[index] = isChecked
            bond.update()
        }
    }
    bind(bond) {
        val value = bond.get()[index]
        if (isChecked != value) {
            isChecked = value;
        }
    }
}

inline fun CheckBox.bindArray(bond: KObservableInterface<Array<Boolean>>, index: Int) {
    this.onCheckedChange {
        buttonView: android.widget.CompoundButton?, isChecked: Boolean ->
        Unit
        if (isChecked != bond.get()[index]) {
            bond.get()[index] = isChecked
            bond.update()
        }
    }
    bind(bond) {
        val value = bond.get()[index]
        if (isChecked != value) {
            isChecked = value;
        }
    }
}

/**
 * Makes this [TextView] display the value of the bond.
 */
inline fun TextView.bindString(bond: KObservableInterface<String>) {
    bind(bond) {
        this.text = bond.get()
    }
}

/**
 * Makes this [TextView] display the value of the bond.
 */
inline fun TextView.bindAny(bond: KObservableInterface<Any>) {
    bind(bond) {
        this.text = bond.get().toString()
    }
}

/**
 * Binds this [Spinner] two way to the bond.
 * When the user picks a new value from the spinner, the value of the bond will change to the index of the new value.
 * When the value of the bond changes, the item will shown will be updated.
 */
inline fun Spinner.bindIndex(bond: KObservableInterface<Int>) {
    bind(bond) {
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

inline fun <T> Spinner.bindList(bond: KObservableInterface<T>, list: List<T>) {
    this.onItemSelectedListener {
        onItemSelected { adapterView, view, index, id ->
            bond.set(list[index])
        }
    }
    bind(bond) {
        if (it == null) return@bind
        val index = list.indexOf(it)
        if (index == -1) return@bind
        setSelection(index)
    }
}

/**
 * Binds this [RadioButton] two way to the bond.
 * When the user picks this radio button, [bond] is set to [value]
 * When the value of the bond changes, it will be shown as checked if they are equal.
 */
inline fun <T> RadioButton.bindValue(bond: KObservableInterface<T>, value: T) {
    bind(bond) {
        isChecked = value == bond.get()
    }
    onCheckedChange { compoundButton, checked ->
        if (checked && bond.get() != value) {
            bond.set(value)
        }
    }
}

inline fun <T> ListView.bindArray(activity: VCActivity, bond: KObservableInterface<Array<T>>, noinline makeView: (KObservableInterface<T>) -> View) {
    val thisAdapter = LightningAdapter(ArrayList(), makeView)
    adapter = thisAdapter
    bind(bond) {
        thisAdapter.list = it.toMutableList()
    }
}

inline fun <T, L : List<T>> ListView.bindList(activity: VCActivity, bond: KObservableInterface<L>, noinline makeView: (KObservableInterface<T>) -> View) {
    val thisAdapter = LightningAdapter(ArrayList(), makeView)
    adapter = thisAdapter
    bind(bond) { list ->
        thisAdapter.list = list
    }
}

inline fun <T, L : List<T>> ListView.bindNullableList(activity: VCActivity, bond: KObservableInterface<L?>, noinline makeView: (KObservableInterface<T>) -> View) {
    val thisAdapter = LightningAdapter(ArrayList(), makeView)
    adapter = thisAdapter
    bind(bond) { list ->
        if (list == null) {
            thisAdapter.list = ArrayList()
        } else {
            thisAdapter.list = list
        }
    }
}