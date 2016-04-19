package com.lightningkite.kotlincomponents.ui

import android.content.Context
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

    var inputLayoutStyle: TextInputLayout.() -> Unit = {}
    var editTextStyle: EditText.() -> Unit = {}
    var buttonStyle: Button.() -> Unit = {}

    val isPassingObs = KObservable(true)
    val errors = HashMap<View, CharSequence?>()
    fun setError(view: View, error: CharSequence?) {
        errors[view] = error
        isPassingObs.set(!errors.values.any { it != null })
    }

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

    inline fun ViewGroup.fieldDouble(obs: KObservableInterface<Double>, format: NumberFormat, hint: Int, setup: TextInputLayout.() -> Unit): TextInputLayout {
        return textInputLayout {
            formPadding()
            hintResource = hint
            textInputEditText {
                bindDouble(obs, format)
                editTextStyle()
                setup()
            }
            inputLayoutStyle()
        }
    }

    inline fun ViewGroup.fieldFloat(obs: KObservableInterface<Float>, format: NumberFormat, hint: Int, setup: TextInputLayout.() -> Unit): TextInputLayout {
        return textInputLayout {
            formPadding()
            hintResource = hint
            textInputEditText {
                bindFloat(obs, format)
                editTextStyle()
                setup()
            }
            inputLayoutStyle()
        }
    }

    inline fun ViewGroup.fieldInt(obs: KObservableInterface<Int>, format: NumberFormat, hint: Int, setup: TextInputLayout.() -> Unit): TextInputLayout {
        return textInputLayout {
            formPadding()
            hintResource = hint
            textInputEditText {
                bindInt(obs, format)
                editTextStyle()
                setup()
            }
            inputLayoutStyle()
        }
    }

    inline fun ViewGroup.fieldNullableInt(obs: KObservableInterface<Int?>, format: NumberFormat, hint: Int, setup: TextInputLayout.() -> Unit): TextInputLayout {
        return textInputLayout {
            formPadding()
            hintResource = hint
            textInputEditText {
                bindNullableInt(obs, format)
                editTextStyle()
                setup()
            }
            inputLayoutStyle()
        }
    }

    inline fun ViewGroup.fieldNullableFloat(obs: KObservableInterface<Float?>, format: NumberFormat, hint: Int, setup: TextInputLayout.() -> Unit): TextInputLayout {
        return textInputLayout {
            formPadding()
            hintResource = hint
            textInputEditText {
                bindNullableFloat(obs, format)
                editTextStyle()
                setup()
            }
            inputLayoutStyle()
        }
    }

    inline fun ViewGroup.fieldString(obs: KObservableInterface<String>, hint: Int, type: Int, setup: TextInputLayout.() -> Unit): TextInputLayout {
        return textInputLayout {
            formPadding()
            minimumHeight = defaultMinimumHeight
            hintResource = hint
            textInputEditText {
                inputType = type
                bindString(obs)
                editTextStyle()
                setup()
            }
            inputLayoutStyle()
        }
    }

    inline fun ViewGroup.email(obs: KObservableInterface<String>, hint: Int, blankError: Int, notEmailError: Int) = fieldString(
            obs,
            hint,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
            {
                bind(obs) {
                    if (it.isEmpty()) errorResource = blankError
                    else if (!it.isEmail()) errorResource = notEmailError
                    else error = null
                    setError(this, error)
                }
            }
    )

    inline fun ViewGroup.password(obs: KObservableInterface<String>, hint: Int, blankError: Int) = fieldString(
            obs,
            hint,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            {
                bind(obs) {
                    if (it.isEmpty()) errorResource = blankError
                    else error = null
                    setError(this, error)
                }
            }
    )

    inline fun ViewGroup.password(obs: KObservableInterface<String>, hint: Int, blankError: Int, minLength: Int, tooShortError: Int) = fieldString(
            obs,
            hint,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            {
                bind(obs) {
                    if (it.isEmpty()) errorResource = blankError
                    else if (it.length < minLength) errorResource = tooShortError
                    else error = null
                    setError(this, error)
                }
            }
    )

    inline fun ViewGroup.confirmPassword(password: KObservableInterface<String>, confirm: KObservable<String>, hint: Int, blankError: Int, notMatchingError: Int) = fieldString(
            confirm,
            hint,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            {
                bind(password, confirm) { pass, conf ->
                    if (conf.isEmpty()) errorResource = blankError
                    else if (pass != conf) errorResource = notMatchingError
                    else error = null
                    setError(this, error)
                }
            }
    )

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

    inline fun ViewGroup.submit(text: Int, setup: ProgressButton.() -> Unit) = progressButton(text) {
        button.lparams(matchParent, matchParent) { formMargins() }
        button.minimumHeight = defaultMinimumHeight
        button.buttonStyle()
        bind(isPassingObs) {
            button.isEnabled = it
        }
        setup()
    }
}

inline fun ViewManager.formLayout(init: FormLayout.() -> Unit) = ankoView({ FormLayout(it) }, init)