package com.lightningkite.kotlincomponents.observable

import android.text.InputType
import android.view.View
import android.widget.*
import com.lightningkite.kotlincomponents.adapter.LightningAdapter
import com.lightningkite.kotlincomponents.ui.isAttachedToWindowCompat
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import org.jetbrains.anko.*
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

/**
 * Various extension functions to support bonds.
 * Created by jivie on 7/22/15.
 */

var bindings = 0

@Suppress("NOTHING_TO_INLINE")
inline private fun View.addAttachListener(listener: View.OnAttachStateChangeListener) {
    if (isAttachedToWindowCompat()) listener.onViewAttachedToWindow(this)
    addOnAttachStateChangeListener(listener)
}

fun <T> View.listen(observable: MutableCollection<T>, item: T) {
    addAttachListener(object : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View?) {
            //println("bindings: ${--bindings}")
            observable.remove(item)
            //this@bind.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
            //println("bindings: ${++bindings}")
            observable.add(item)
        }
    })
}

fun <T> View.bind(observable: MutableCollection<(T) -> Unit>, init: T, item: (T) -> Unit) {
    addAttachListener(object : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View?) {
            //println("bindings: ${--bindings}")
            observable.remove(item)
            //this@bind.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
            //println("bindings: ${++bindings}")
            observable.add(item)
            item(init)
        }
    })
}

fun <A, B> View.listen(observableA: KObservableInterface<A>, observableB: KObservableInterface<B>, action: (A, B) -> Unit) {
    addAttachListener(object : View.OnAttachStateChangeListener {

        val itemA = { item: A -> action(item, observableB.get()) }
        val itemB = { item: B -> action(observableA.get(), item) }

        override fun onViewDetachedFromWindow(v: View?) {
            //println("bindings: ${--bindings}")
            observableA.remove(itemA)
            observableB.remove(itemB)
            //this@bind.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
            //println("bindings: ${++bindings}")
            observableA.add(itemA)
            observableB.add(itemB)
        }
    })
}

fun <T> View.bind(observable: KObservableListInterface<T>, item: (KObservableListInterface<T>) -> Unit) {
    bind(observable.onUpdate, item)
}

fun <T> View.bind(observable: KObservableInterface<T>, item: (T) -> Unit) {
    addAttachListener(object : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View?) {
            //println("bindings: ${--bindings}")
            observable.remove(item)
            //this@bind.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
            //println("bindings: ${++bindings}")
            item(observable.get())
            observable.add(item)
        }
    })
}

fun <A, B> View.bind(observableA: KObservableInterface<A>, observableB: KObservableInterface<B>, action: (A, B) -> Unit) {
    addAttachListener(object : View.OnAttachStateChangeListener {

        val itemA = { item: A -> action(item, observableB.get()) }
        val itemB = { item: B -> action(observableA.get(), item) }

        override fun onViewDetachedFromWindow(v: View?) {
            //println("bindings: ${--bindings}")
            observableA.remove(itemA)
            observableB.remove(itemB)
            //this@bind.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
            //println("bindings: ${++bindings}")
            action(observableA.get(), observableB.get())
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
    addAttachListener(object : View.OnAttachStateChangeListener {

        val itemA = { item: A -> action(item, observableB.get(), observableC.get()) }
        val itemB = { item: B -> action(observableA.get(), item, observableC.get()) }
        val itemC = { item: C -> action(observableA.get(), observableB.get(), item) }

        override fun onViewDetachedFromWindow(v: View?) {
            //println("bindings: ${--bindings}")
            observableA.remove(itemA)
            observableB.remove(itemB)
            observableC.remove(itemC)
            //this@bind.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
            //println("bindings: ${++bindings}")
            action(observableA.get(), observableB.get(), observableC.get())
            observableA.add(itemA)
            observableB.add(itemB)
            observableC.add(itemC)
        }
    })
}

fun <A, B> View.bind(observableA: KObservableInterface<A>, observableB: KObservableInterface<B>, action: () -> Unit) {
    addAttachListener(object : View.OnAttachStateChangeListener {

        val itemA = { item: A -> action() }
        val itemB = { item: B -> action() }

        override fun onViewDetachedFromWindow(v: View?) {
            //println("bindings: ${--bindings}")
            observableA.remove(itemA)
            observableB.remove(itemB)
            //this@bind.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
            //println("bindings: ${++bindings}")
            action()
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
    addAttachListener(object : View.OnAttachStateChangeListener {

        val itemA = { item: A -> action() }
        val itemB = { item: B -> action() }
        val itemC = { item: C -> action() }

        override fun onViewDetachedFromWindow(v: View?) {
            //println("bindings: ${--bindings}")
            observableA.remove(itemA)
            observableB.remove(itemB)
            observableC.remove(itemC)
            //this@bind.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
            //println("bindings: ${++bindings}")
            action()
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
@Suppress("NOTHING_TO_INLINE")
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
@Suppress("NOTHING_TO_INLINE")
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
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindInt(bond: KObservableInterface<Int>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value: Int? = null
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->

            value = null

            try {
                value = format.parse(charSequence.toString()).toInt()
            } catch(e: ParseException) {
                //do nothing.
            }

            try {
                value = charSequence.toString().toInt()
            } catch(e: NumberFormatException) {
                //do nothing.
            }

            if (value == null) {
                textColor = 0xFF0000.opaque
            } else {
                textColor = originalTextColor
                if (bond.get() != value) {
                    bond.set(value!!)
                }
            }
        }
    }
    bind(bond) {
        if (bond.get() != value) {
            this.setText(format.format(bond.get()))
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the integer here will be updated.
 */
inline fun EditText.bindNullableInt(bond: KObservableInterface<Int?>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value: Int? = null
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->

            value = null

            try {
                value = format.parse(charSequence.toString()).toInt()
            } catch(e: ParseException) {
                //do nothing.
            }

            try {
                value = charSequence.toString().toInt()
            } catch(e: NumberFormatException) {
                //do nothing.
            }

            textColor = originalTextColor
            if (bond.get() != value) {
                bond.set(value!!)
            }
        }
    }
    bind(bond) {
        if (bond.get() != value) {
            if (bond.get() == null) this.setText("")
            else this.setText(format.format(bond.get()))
        }
    }
}


/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the number here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindNullableFloat(bond: KObservableInterface<Float?>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value: Float? = null
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->

            value = null

            try {
                value = format.parse(charSequence.toString()).toFloat()
            } catch(e: ParseException) {
                //do nothing.
            }

            try {
                value = charSequence.toString().toFloat()
            } catch(e: NumberFormatException) {
                //do nothing.
            }

            textColor = originalTextColor
            if (bond.get() != value) {
                bond.set(value)
            }
        }
    }
    bind(bond) {
        if (bond.get() != value) {
            if (bond.get() == null) this.setText("")
            else this.setText(format.format(bond.get()))
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the number here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindNullableDouble(bond: KObservableInterface<Double?>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value: Double? = null
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->

            value = null

            try {
                value = format.parse(charSequence.toString()).toDouble()
            } catch(e: ParseException) {
                //do nothing.
            }

            try {
                value = charSequence.toString().toDouble()
            } catch(e: NumberFormatException) {
                //do nothing.
            }

            textColor = originalTextColor
            if (bond.get() != value) {
                bond.set(value)
            }
        }
    }
    bind(bond) {
        if (bond.get() != value) {
            if (bond.get() == null) this.setText("")
            else this.setText(format.format(bond.get()))
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the number here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindFloat(bond: KObservableInterface<Float>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value = Float.NaN
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->

            value = Float.NaN

            try {
                value = format.parse(charSequence.toString()).toFloat()
            } catch(e: ParseException) {
                //do nothing.
            }

            try {
                value = charSequence.toString().toFloat()
            } catch(e: NumberFormatException) {
                //do nothing.
            }

            if (value.isNaN()) {
                textColor = 0xFF0000.opaque
            } else {
                textColor = originalTextColor
                if (bond.get() != value) {
                    bond.set(value)
                }
            }
        }
    }
    bind(bond) {
        if (bond.get() != value) {
            this.setText(format.format(bond.get()))
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the number here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindDouble(bond: KObservableInterface<Double>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value = Double.NaN
    textChangedListener {
        onTextChanged { charSequence, start, before, count ->

            value = Double.NaN

            try {
                value = format.parse(charSequence.toString()).toDouble()
            } catch(e: ParseException) {
                //do nothing.
            }

            try {
                value = charSequence.toString().toDouble()
            } catch(e: NumberFormatException) {
                //do nothing.
            }

            if (value.isNaN()) {
                textColor = 0xFF0000.opaque
            } else {
                textColor = originalTextColor
                if (bond.get() != value) {
                    bond.set(value)
                }
            }
        }
    }
    bind(bond) {
        if (bond.get() != value) {
            this.setText(format.format(bond.get()))
        }
    }
}

/**
 * Binds this [Switch] two way to the bond.
 */
@Suppress("NOTHING_TO_INLINE")
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

@Suppress("NOTHING_TO_INLINE")
inline fun Switch.bindArray(bond: KObservableInterface<Array<Boolean>>, index: Int) {
    this.onCheckedChange {
        buttonView: android.widget.CompoundButton?, isChecked: Boolean ->
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

@Suppress("NOTHING_TO_INLINE")
inline fun CheckBox.bindBoolean(bond: KObservableInterface<Boolean>) {
    this.onCheckedChange {
        buttonView: android.widget.CompoundButton?, isChecked: Boolean ->
        if (isChecked != bond.get()) {
            bond.set(isChecked)
        }
    }
    bind(bond) {
        val value = bond.get()
        if (isChecked != value) {
            isChecked = value;
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun CheckBox.bindArray(bond: KObservableInterface<Array<Boolean>>, index: Int) {
    this.onCheckedChange {
        buttonView: android.widget.CompoundButton?, isChecked: Boolean ->
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

inline fun CheckBox.bindList(bond: KObservableInterface<MutableList<Boolean>>, index: Int) {
    this.onCheckedChange {
        buttonView: android.widget.CompoundButton?, isChecked: Boolean ->
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

inline fun <T> CheckBox.bindList(list: KObservableList<T>, item: T) {
    this.onCheckedChange {
        buttonView: android.widget.CompoundButton?, isChecked: Boolean ->

        val index = list.indexOfFirst { it == item }
        if (isChecked != (index != -1)) {
            if (index != -1) {
                list.removeAt(index)
            } else {
                list.add(item)
            }
        }
    }
    bind(list) {
        val index = list.indexOfFirst { it == item }
        if (isChecked != (index != -1)) {
            isChecked = (index != -1);
        }
    }
}

inline fun <T> CheckBox.bindList(list: KObservableList<T>, item: T, crossinline matches: (T, T) -> Boolean) {
    this.onCheckedChange {
        buttonView: android.widget.CompoundButton?, isChecked: Boolean ->

        val index = list.indexOfFirst { matches(it, item) }
        if (isChecked != (index != -1)) {
            if (index != -1) {
                list.removeAt(index)
            } else {
                list.add(item)
            }
        }
    }
    bind(list) {
        val index = list.indexOfFirst { matches(it, item) }
        if (isChecked != (index != -1)) {
            isChecked = (index != -1);
        }
    }
}

/**
 * Makes this [TextView] display the value of the bond.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun TextView.bindString(bond: KObservableInterface<String>) {
    bind(bond) {
        this.text = bond.get()
    }
}

/**
 * Makes this [TextView] display the value of the bond.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> TextView.bindAny(bond: KObservableInterface<T>) {
    bind(bond) {
        this.text = bond.get().toString()
    }
}

/**
 * Binds this [Spinner] two way to the bond.
 * When the user picks a new value from the spinner, the value of the bond will change to the index of the new value.
 * When the value of the bond changes, the item will shown will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
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

@Suppress("NOTHING_TO_INLINE")
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

@Suppress("NOTHING_TO_INLINE")
inline fun <T, E> Spinner.bindList(bond: KObservableInterface<T>, list: List<E>, crossinline conversion: (E) -> T) {
    this.onItemSelectedListener {
        onItemSelected { adapterView, view, index, id ->
            bond.set(conversion(list[index]))
        }
    }
    bind(bond) {
        if (it == null) return@bind
        val index = list.indexOfFirst { item -> it == conversion(item) }
        if (index == -1) return@bind
        setSelection(index)
    }
}

/**
 * Binds this [RadioButton] two way to the bond.
 * When the user picks this radio button, [bond] is set to [value]
 * When the value of the bond changes, it will be shown as checked if they are equal.
 */
@Suppress("NOTHING_TO_INLINE")
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

/**
 * Binds this [RadioButton] two way to the bond.
 * When the user picks this radio button, [bond] is set to [value]
 * When the value of the bond changes, it will be shown as checked if they are equal.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T, A : T> RadioButton.bindValue(bond: KObservableInterface<T>, otherBond: KObservableInterface<A>) {
    bind(bond, otherBond) { currentValue, myValue ->
        isChecked = currentValue == myValue
    }
    onCheckedChange { compoundButton, checked ->
        if (checked && bond.get() != otherBond.get()) {
            bond.set(otherBond.get())
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> ListView.bindArray(activity: VCActivity, bond: KObservableInterface<Array<T>>, noinline makeView: (KObservableInterface<T>) -> View) {
    val thisAdapter = LightningAdapter(ArrayList(), makeView)
    adapter = thisAdapter
    bind(bond) {
        thisAdapter.list = it.toMutableList()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T, L : List<T>> ListView.bindList(activity: VCActivity, bond: KObservableInterface<L>, noinline makeView: (KObservableInterface<T>) -> View) {
    val thisAdapter = LightningAdapter(ArrayList(), makeView)
    adapter = thisAdapter
    bind(bond) { list ->
        thisAdapter.list = list
    }
}

@Suppress("NOTHING_TO_INLINE")
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