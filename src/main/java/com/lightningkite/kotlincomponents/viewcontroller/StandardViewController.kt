package com.lightningkite.kotlincomponents.viewcontroller

import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lightningkite.kotlincomponents.Disposable
import com.lightningkite.kotlincomponents.adapter.AdaptableViewController
import com.lightningkite.kotlincomponents.adapter.ViewControllerAdapter
import com.lightningkite.kotlincomponents.observable.KObservable
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContainer
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCView
import org.jetbrains.anko.onCheckedChange
import org.jetbrains.anko.opaque
import org.jetbrains.anko.textChangedListener
import org.jetbrains.anko.textColor
import java.text.NumberFormat
import java.util.*

/**
 * Created by jivie on 1/19/16.
 */
abstract class StandardViewController() : ViewController {

    val onMake: ArrayList<(View) -> Unit> = ArrayList()
    val onUnmake: ArrayList<(View) -> Unit> = ArrayList()
    val onDispose: ArrayList<() -> Unit> = ArrayList()

    /**
     * Adds the item to the collection immediately, but removes it when [unmake] is called.
     * The primary use of this is binding things in [make] that need to be removed when [unmake] is called.
     */
    fun <T> connect(collection: MutableCollection<T>, item: T): T {
        collection.add(item)
        onUnmake.add {
            collection.remove(item)
        }
        return item
    }

    /**
     * Adds the item to the collections immediately, but removes the item from all of the collections when [unmake] is called.
     * The primary use of this is binding things in [make] that need to be removed when [unmake] is called.
     */
    fun <T> connectMany(vararg collections: MutableCollection<T>, item: T): T {
        for (collection in collections) {
            collection.add(item)
        }
        onUnmake.add {
            for (collection in collections) {
                collection.remove(item)
            }
        }
        return item
    }

    abstract fun makeView(activity: VCActivity): View
    final override fun make(activity: VCActivity): View {
        val view = makeView(activity)
        onMake.forEach { it(view) }
        onMake.clear()
        return view
    }

    override fun unmake(view: View) {
        onUnmake.forEach { it(view) }
        onUnmake.clear()
        super.unmake(view)
    }

    override fun dispose() {
        onDispose.forEach { it() }
        onDispose.clear()
        super.dispose()
    }

    fun <T : Disposable> autoDispose(vc: T): T {
        onDispose.add { vc.dispose() }
        return vc
    }

    fun ViewGroup.viewContainer(container: VCContainer): VCView {
        val vcview = VCView(context as VCActivity)
        vcview.attach(container)
        onUnmake.add {
            vcview.detatch()
        }
        addView(vcview)
        return vcview
    }

    ////////////////////////////////////////////////////////////////////
    //HERE BEGINS THE LARGE AMOUNT OF CONVENIENCE OBSERVABLE FUNCTIONS//
    ////////////////////////////////////////////////////////////////////

    /**
     * Binds this [EditText] two way to the bond.
     * When the user edits this, the value of the bond will change.
     * When the value of the bond changes, the text here will be updated.
     */
    public fun EditText.bindString(bond: KObservable<String>) {
        setText(bond.get())
        textChangedListener {
            onTextChanged { charSequence, start, before, count ->
                if (bond.get() != charSequence) {
                    bond.set(charSequence.toString())
                }
            }
        }
        connect(bond) {
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
    public fun EditText.bindNullableString(bond: KObservable<String?>) {
        setText(bond.get())
        textChangedListener {
            onTextChanged { charSequence, start, before, count ->
                if (bond.get() != charSequence) {
                    bond.set(charSequence.toString())
                }
            }
        }
        connect(bond) {
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
    public fun EditText.bindInt(bond: KObservable<Int>) {
        inputType = (inputType and 0xFFFFFFF0.toInt()) or InputType.TYPE_CLASS_NUMBER
        setText(bond.get().toString())
        textChangedListener {
            onTextChanged { charSequence, start, before, count ->
                if (!bond.get().toString().equals(charSequence)) {
                    bond.set(charSequence.toString().toInt())
                }
            }
        }
        connect(bond) {
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
    public fun EditText.bindFloat(bond: KObservable<Float>, format: NumberFormat) {
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
        connect(bond) {
            val value = text.toString().toFloat()
            if (bond.get() != value) {
                this.setText(format.format(bond.get()))
            }
        }
    }

    /**
     * Binds this [Switch] two way to the bond.
     */
    public fun Switch.bind(bond: KObservable<Boolean>) {
        this.onCheckedChange {
            buttonView: android.widget.CompoundButton?, isChecked: Boolean ->
            Unit
            if (isChecked != bond.get()) {
                bond.set(isChecked);
            }
        }
        connect(bond) {
            if (isChecked != bond.get()) {
                isChecked = bond.get();
            }
        }
    }

    public fun Switch.bindArray(bond: KObservable<Array<Boolean>>, index: Int) {
        this.onCheckedChange {
            buttonView: android.widget.CompoundButton?, isChecked: Boolean ->
            Unit
            if (isChecked != bond.get()[index]) {
                bond.get()[index] = isChecked
                bond.update()
            }
        }
        connect(bond) {
            val value = bond.get()[index]
            if (isChecked != value) {
                isChecked = value;
            }
        }
    }

    public fun CheckBox.bindArray(bond: KObservable<Array<Boolean>>, index: Int) {
        this.onCheckedChange {
            buttonView: android.widget.CompoundButton?, isChecked: Boolean ->
            Unit
            if (isChecked != bond.get()[index]) {
                bond.get()[index] = isChecked
                bond.update()
            }
        }
        connect(bond) {
            val value = bond.get()[index]
            if (isChecked != value) {
                isChecked = value;
            }
        }
    }

    /**
     * Makes this [TextView] display the value of the bond.
     */
    public fun TextView.bindString(bond: KObservable<String>) {
        connect(bond) {
            this.text = bond.get()
        }
    }

    /**
     * Makes this [TextView] display the value of the bond.
     */
    public fun TextView.bindAny(bond: KObservable<Any>) {
        connect(bond) {
            this.text = bond.get().toString()
        }
    }

    /**
     * Binds this [Spinner] two way to the bond.
     * When the user picks a new value from the spinner, the value of the bond will change to the index of the new value.
     * When the value of the bond changes, the item will shown will be updated.
     */
    public fun Spinner.bindIndex(bond: KObservable<Int>) {
        connect(bond) {
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
    public fun <T> RadioButton.bind(bond: KObservable<T>, value: T) {
        connect(bond) {
            isChecked = value == bond.get()
        }
        onCheckedChange { compoundButton, checked ->
            if (checked && bond.get() != value) {
                bond.set(value)
            }
        }
    }

    public fun <T> ListView.bindArray(activity: VCActivity, bond: KObservable<Array<T>>, makeView: AdaptableViewController<T>.() -> View) {
        val thisAdapter = ViewControllerAdapter.quick(activity, ArrayList(), makeView)
        adapter = thisAdapter
        connect(bond) {
            thisAdapter.list = it.toArrayList()
        }
    }

    public fun <T> ListView.bindList(activity: VCActivity, bond: KObservable<in List<T>>, makeView: AdaptableViewController<T>.() -> View) {
        val thisAdapter = ViewControllerAdapter.quick(activity, ArrayList(), makeView)
        adapter = thisAdapter
        connect(bond) { list ->
            thisAdapter.list = list
        }
    }

    public fun <T> ListView.bindNullableList(activity: VCActivity, bond: KObservable<in List<T>?>, makeView: AdaptableViewController<T>.() -> View) {
        val thisAdapter = ViewControllerAdapter.quick(activity, ArrayList(), makeView)
        adapter = thisAdapter
        connect(bond) { list ->
            if (list == null) {
                thisAdapter.list = ArrayList()
            } else {
                thisAdapter.list = list
            }
        }
    }
}