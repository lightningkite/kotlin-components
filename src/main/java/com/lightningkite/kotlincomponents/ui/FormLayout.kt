package com.lightningkite.kotlincomponents.ui

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.lightningkite.kotlincomponents.isEmail
import com.lightningkite.kotlincomponents.observable.*
import com.lightningkite.kotlincomponents.selectableItemBackgroundResource
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.textInputLayout
import java.text.NumberFormat
import java.util.*

/**
 * Created by jivie on 3/15/16.
 */
class FormLayout(ctx: Context) : _LinearLayout(ctx) {

    init {
        orientation = LinearLayout.VERTICAL
    }

    var defaultMinimumHeight = dip(50)
    var defaultHorizontalPadding = dip(16)
    var defaultVerticalPadding = dip(8)

    var makeField: ViewGroup.(hint: Int, innerViewMaker: ViewGroup.() -> View) -> View = { hint, innerViewMaker ->
        linearLayout {
            formPadding()
            minimumHeight = defaultMinimumHeight
            gravity = Gravity.CENTER

            textView(hint).lparams(0, wrapContent, 1f)

            innerViewMaker()
        }
    }

    var makeTextField: ViewGroup.(hint: Int, editTextSetup: EditText.() -> Unit) -> View = { hint, editTextSetup ->
        textInputLayout {
            formPadding()
            hintResource = hint
            textInputEditText {
                editTextSetup()
            }
        }
    }

    inline fun defaultStyle(crossinline styleTextField: EditText.() -> Unit) {
        makeTextField = { hint, editTextSetup ->
            editText() {
                hintResource = hint
                styleTextField()
                editTextSetup()
            }.apply {
                layoutParams = (layoutParams as MarginLayoutParams).apply {
                    formMargins()
                }
            }
        }
    }

    inline fun materialStyle(crossinline editTextStyle: TextInputEditText.() -> Unit, crossinline inputLayoutStyle: TextInputLayout.() -> Unit) {
        makeTextField = { hint, editTextSetup ->
            textInputLayout {
                formPadding()
                hintResource = hint
                textInputEditText {
                    editTextStyle()
                    editTextSetup()
                }
                inputLayoutStyle()
            }
        }
    }

    var buttonStyle: Button.() -> Unit = {}

    val isPassingObs = KObservable(true)
    val errors = HashMap<View, CharSequence?>()

    inline fun View.formPadding() {
        leftPadding = defaultHorizontalPadding
        rightPadding = defaultHorizontalPadding
        topPadding = defaultVerticalPadding
        bottomPadding = defaultVerticalPadding
    }

    inline fun MarginLayoutParams.formMargins() {
        leftMargin = defaultHorizontalPadding
        rightMargin = defaultHorizontalPadding
        topMargin = defaultVerticalPadding
        bottomMargin = defaultVerticalPadding
    }

    override fun generateDefaultLayoutParams(): LayoutParams? {
        return super.generateDefaultLayoutParams().apply {
            width = matchParent
        }
    }

    var View.formError: CharSequence?
        get() = errors[this]
        set(value) {
            errors[this] = value
            if (this is TextInputLayout) {
                this.error = value
            }
            val parent = this.parent
            if (parent is TextInputLayout) {
                parent.error = value
            }
            isPassingObs.set(!errors.values.any { it != null })
        }
    var View.formErrorResource: Int?
        get() = throw IllegalAccessException()
        set(resource) {
            val value = if (resource != null) resources.getString(resource) else null
            errors[this] = value
            if (this is TextInputLayout) {
                this.error = value
            }
            val parent = this.parent
            if (parent is TextInputLayout) {
                parent.error = value
            }
            isPassingObs.set(!errors.values.any { it != null })
        }

    inline fun ViewGroup.fieldDouble(obs: KObservableInterface<Double>, format: NumberFormat, hint: Int, crossinline setup: EditText.() -> Unit): View {
        return makeTextField(hint) {
            bindDouble(obs, format)
            setup()
        }
    }

    inline fun ViewGroup.fieldFloat(obs: KObservableInterface<Float>, format: NumberFormat, hint: Int, crossinline setup: EditText.() -> Unit): View {
        return makeTextField(hint) {
            bindFloat(obs, format)
            setup()
        }
    }

    inline fun ViewGroup.fieldInt(obs: KObservableInterface<Int>, format: NumberFormat, hint: Int, crossinline setup: EditText.() -> Unit): View {
        return makeTextField(hint) {
            bindInt(obs, format)
            setup()
        }
    }

    inline fun ViewGroup.fieldNullableInt(obs: KObservableInterface<Int?>, format: NumberFormat, hint: Int, crossinline setup: EditText.() -> Unit): View {
        return makeTextField(hint) {
            bindNullableInt(obs, format)
            setup()
        }
    }

    inline fun ViewGroup.fieldNullableFloat(obs: KObservableInterface<Float?>, format: NumberFormat, hint: Int, crossinline setup: EditText.() -> Unit): View {
        return makeTextField(hint) {
            bindNullableFloat(obs, format)
            setup()
        }
    }

    inline fun ViewGroup.fieldString(obs: KObservableInterface<String>, hint: Int, type: Int, crossinline setup: EditText.() -> Unit): View {
        return makeTextField(hint) {
            bindString(obs)
            inputType = type
            setup()
        }
    }

    inline fun ViewGroup.email(obs: KObservableInterface<String>, hint: Int, blankError: Int, notEmailError: Int) = fieldString(
            obs,
            hint,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
            {
                bind(obs) {
                    formErrorResource =
                            if (it.isEmpty()) blankError
                            else if (!it.isEmail()) notEmailError
                            else null
                }
            }
    )

    inline fun ViewGroup.password(obs: KObservableInterface<String>, hint: Int, blankError: Int) = fieldString(
            obs,
            hint,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            {
                bind(obs) {
                    formErrorResource =
                            if (it.isEmpty()) blankError
                            else null
                }
            }
    )

    inline fun ViewGroup.password(obs: KObservableInterface<String>, hint: Int, blankError: Int, minLength: Int, tooShortError: Int) = fieldString(
            obs,
            hint,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            {
                bind(obs) {
                    formErrorResource =
                            if (it.isEmpty()) blankError
                            else if (it.length < minLength) tooShortError
                            else null
                }
            }
    )

    inline fun ViewGroup.confirmPassword(password: KObservableInterface<String>, confirm: KObservable<String>, hint: Int, blankError: Int, notMatchingError: Int) = fieldString(
            confirm,
            hint,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            {
                bind(password, confirm) { pass, conf ->
                    formErrorResource =
                            if (conf.isEmpty()) blankError
                            else if (pass != conf) notMatchingError
                            else null
                }
            }
    )

    inline fun ViewGroup.specialField(label: Int, noinline maker: ViewGroup.() -> View): View {
        return makeField(label, maker)
    }

    inline fun ViewGroup.switchLayout(observable: KObservableInterface<Boolean>, label: Int) {
        linearLayout {
            formPadding()
            minimumHeight = defaultMinimumHeight
            gravity = Gravity.CENTER

            textView(label).lparams(0, wrapContent, 1f)

            val s = switch() {
                bind(observable)
            }

            backgroundResource = selectableItemBackgroundResource
            onClick {
                s.toggle()
            }

        }
    }

    inline fun ViewGroup.submit(text: Int, setup: ProgressButton.() -> Unit) = formProgressButton(text) {
        bind(isPassingObs) {
            button.isEnabled = it
        }
        setup()
    }

    inline fun ViewGroup.formProgressButton(text: Int, setup: ProgressButton.() -> Unit) = progressButton(text) {
        button.lparams(matchParent, matchParent) { formMargins() }
        button.minimumHeight = defaultMinimumHeight
        button.buttonStyle()
        setup()
    }

    inline fun _LinearLayout.formButton(text: Int, setup: Button.() -> Unit): Button = button(text) {
        minimumHeight = defaultMinimumHeight
        buttonStyle()
        setup()
    }.lparams(matchParent, wrapContent) {
        formMargins()
    }
}

inline fun ViewManager.formLayout(init: FormLayout.() -> Unit) = ankoView({ FormLayout(it) }, init)