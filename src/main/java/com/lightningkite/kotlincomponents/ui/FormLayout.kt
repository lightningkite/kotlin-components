package com.lightningkite.kotlincomponents.ui

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.text.InputType
import android.view.View
import android.view.ViewManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.lightningkite.kotlincomponents.isEmail
import com.lightningkite.kotlincomponents.observable.KObservable
import com.lightningkite.kotlincomponents.observable.bind
import com.lightningkite.kotlincomponents.observable.bindString
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.textInputLayout
import java.util.*

/**
 * Created by jivie on 3/15/16.
 */
class FormLayout(ctx: Context) : _LinearLayout(ctx) {

    init {
        orientation = LinearLayout.VERTICAL
    }

    var editTextStyle: EditText.() -> Unit = {}
    var buttonStyle: Button.() -> Unit = {}

    val isPassingObs = KObservable(false)
    val errors = HashMap<View, CharSequence?>()
    fun setError(view: View, error: CharSequence?) {
        errors[view] = error
        isPassingObs.set(!errors.values.any { it != null })
    }

    inline fun field(obs: KObservable<String>, hint: Int, type: Int, setup: TextInputLayout.() -> Unit): TextInputLayout {
        return textInputLayout {
            hintResource = hint
            editText {
                inputType = type
                bindString(obs)

                setup()
            }
        }.lparams(matchParent, wrapContent) { margin = dip(4) }
    }

    inline fun email(obs: KObservable<String>, hint: Int, blankError: Int, notEmailError: Int) = field(
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

    inline fun password(obs: KObservable<String>, hint: Int, blankError: Int) = field(
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

    inline fun password(obs: KObservable<String>, hint: Int, blankError: Int, minLength: Int, tooShortError: Int) = field(
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

    inline fun confirmPassword(password: KObservable<String>, confirm: KObservable<String>, hint: Int, blankError: Int, notMatchingError: Int) = field(
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

    inline fun submit(text: Int, setup: ProgressButton.() -> Unit) = progressButton(text) {
        padding = dip(4)
        button.buttonStyle()
        bind(isPassingObs) {
            button.isEnabled = it
        }
        setup()
    }.lparams(matchParent, wrapContent)
}

inline fun ViewManager.formLayout(init: FormLayout.() -> Unit) = ankoView({ FormLayout(it) }, init)