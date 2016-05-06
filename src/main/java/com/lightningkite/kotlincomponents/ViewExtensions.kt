package com.lightningkite.kotlincomponents

import android.R
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import android.os.Build
import android.text.Html
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.*
import org.jetbrains.anko.*

/**
 * Created by jivie on 7/16/15.
 */

//public fun View.dip(value: Int): Int = context.dip(value)


@Deprecated("Not useful enough to warrant inclusion.")
fun ViewGroup.MarginLayoutParams.setMarginsDip(context: Context, left: Int, top: Int, right: Int, bottom: Int) {
    setMargins(context.dip(left), context.dip(top), context.dip(right), context.dip(bottom))
}

@Deprecated("Not useful enough to warrant inclusion.")
fun View.setLayoutParamsMargin(context: Context, width: Int, height: Int, left: Int, top: Int, right: Int, bottom: Int) {
    val params = ViewGroup.MarginLayoutParams(
            if (width != matchParent && width != wrapContent && width != 0)
                context.dip(width)
            else
                width,
            if (height != matchParent && height != wrapContent && height != 0)
                context.dip(height)
            else
                height
    )
    params.setMargins(context.dip(left), context.dip(top), context.dip(right), context.dip(bottom))
    layoutParams = params
}

val LinearLayout.horizontal: Int
    @Deprecated("Create using linearLayout.")
    get() = LinearLayout.HORIZONTAL
val LinearLayout.vertical: Int
    @Deprecated("Create the layout using verticalLayout.")
    get() = LinearLayout.VERTICAL

fun EditText.showSoftInput() {
    context.inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
}

fun View.hideSoftInput() {
    context.inputMethodManager.hideSoftInputFromWindow(this.applicationWindowToken, 0)
}

fun Activity.hideSoftInput() {
    inputMethodManager.toggleSoftInput(0, 0)
}

@Deprecated("Use the properly named one, selectableItemBackgroundResource",
        ReplaceWith(
                "selectableItemBackgroundResource",
                "com.lightningkite.kotlincomponents.selectableItemBackgroundResource"
        )
)
val View.selectableItemBackground: Int
    get() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            val outValue = TypedValue();
            context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true);
            return outValue.resourceId
        }
        return 0
    }



fun View.getActivity(): Activity? {
    return context.getActivity()
}

fun Context.getActivity(): Activity? {
    if (this is Activity) {
        return this
    } else if (this is ContextWrapper) {
        return baseContext.getActivity()
    } else {
        return null
    }
}

fun View.postDelayed(milliseconds: Long, action: () -> Unit) {
    postDelayed({ action() }, milliseconds)
}

inline fun <T : View> ViewGroup.addView(view: T, setup: T.() -> Unit): T {
    view.setup();
    addView(view)
    return view
}

inline fun <reified T : View> ViewGroup.addView(setup: T.() -> Unit): T {
    val view = T::class.java.getConstructor(Context::class.java).newInstance(context)
    view.setup();
    addView(view)
    return view
}

@Deprecated("Anko handles this now.")
fun View.onChildrenRecursive(action: (View) -> Unit) {
    action(this)
    if (this is ViewGroup) {
        for (index in 0..this.childCount - 1) {
            getChildAt(index)?.onChildrenRecursive(action)
        }
    }
}

var TextView.html: String get() = throw IllegalAccessException()
    set(value) {
        val newVal = value
                .replace("<li>", "<p>&bull; ")
                .replace("</li>", "</p>")
                .replace("<ul>", "")
                .replace("</ul>", "")
        text = Html.fromHtml(newVal)
    }

private val cachedPoint: Point = Point()
@Deprecated("This is useless for most applications.")
val View.screenSize: Point get() {
    context.windowManager.defaultDisplay.getSize(cachedPoint)
    return cachedPoint
}

val View.parentView: View get() {
    return parent as? View ?: throw IllegalStateException("Parent is not a ViewGroup!")
}

fun <T, A : Adapter> AdapterView<A>.setAdapter(adapter: A, onClickAction: (T) -> Unit) {
    this.adapter = adapter
    this.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> onClickAction(adapter.getItem(position) as T) }
}

fun WebView.javascript(function: String, vararg arguments: Any?) {
    val stringArguments = arguments.map {
        when (it) {
            null -> "null"
            is String -> "\"$it\""
            is Int -> it.toString()
            is Long -> it.toString()
            is Float -> it.toString()
            is Double -> it.toString()
            is Boolean -> it.toString()
            is Char -> "'$it'"
            else -> throw IllegalArgumentException("type: " + it.javaClass.simpleName)
        }
    }
    val call = function + "(" + stringArguments.joinToString(", ") + ");"
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
        evaluateJavascript(call, null);
    } else {
        loadUrl("javascript:" + call);
    }
}

fun ListView.getView(pos: Int): View? {
    val firstListItemPosition = firstVisiblePosition;
    val lastListItemPosition = firstListItemPosition + childCount - 1;

    if (pos < firstListItemPosition || pos > lastListItemPosition ) {
        return null
    } else {
        val childIndex = pos - firstListItemPosition;
        return getChildAt(childIndex);
    }
}

inline fun View.onDetached(crossinline action: () -> Unit) {
    this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View?) {
            action()
        }

        override fun onViewAttachedToWindow(v: View?) {
        }

    })
}

inline fun View.onAttached(crossinline action: () -> Unit) {
    this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View?) {
        }

        override fun onViewAttachedToWindow(v: View?) {
            action()
        }

    })
}