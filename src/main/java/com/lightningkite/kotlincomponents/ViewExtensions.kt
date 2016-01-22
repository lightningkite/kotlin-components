package com.lightningkite.kotlincomponents

import android.R
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.text.Html
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.*
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import org.jetbrains.anko.*
import java.util.*

/**
 * Created by jivie on 7/16/15.
 */

public fun ViewController.inflate(context: Context, layoutResource: Int, init: View.() -> Unit): View {
    val layout = LayoutInflater.from(context).inflate(layoutResource, null);
    layout.init();
    return layout;
}

public fun View.dip(value: Int): Int = context.dip(value)

public fun View.animateHighlight(milliseconds: Long, color: Int, millisecondsTransition: Int = 200) {
    assert(milliseconds > millisecondsTransition * 2) { "The time shown must be at least twice as much as the transition time" }
    val originalBackground = background
    val transition = TransitionDrawable(arrayOf(originalBackground, ColorDrawable(color)))
    transition.isCrossFadeEnabled = false
    background = transition
    transition.startTransition(millisecondsTransition)
    postDelayed(milliseconds - millisecondsTransition) {
        transition.reverseTransition(millisecondsTransition)
        postDelayed(millisecondsTransition.toLong()) {
            background = originalBackground
        }
    }
}

public fun ViewGroup.MarginLayoutParams.setMarginsDip(context: Context, left: Int, top: Int, right: Int, bottom: Int) {
    setMargins(context.dip(left), context.dip(top), context.dip(right), context.dip(bottom))
}

public fun View.setLayoutParamsMargin(context: Context, width: Int, height: Int, left: Int, top: Int, right: Int, bottom: Int) {
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

public val LinearLayout.horizontal: Int  get() = LinearLayout.HORIZONTAL
public val LinearLayout.vertical: Int  get() = LinearLayout.VERTICAL

public var TextView.textColorResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        setTextColor(resources.getColor(value))
    }
public var TextView.hintTextColorResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        setHintTextColor(resources.getColor(value))
    }

val fontCache: HashMap<String, Typeface> = HashMap()
public fun TextView.setFont(fileName: String) {
    typeface = fontCache[fileName] ?: {
        val font = Typeface.createFromAsset(context.assets, fileName)
        fontCache[fileName] = font
        font
    }()
}

public fun EditText.showSoftInput() {
    context.inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
}

public fun View.hideSoftInput() {
    context.inputMethodManager.hideSoftInputFromWindow(this.applicationWindowToken, 0)
}

public fun Activity.hideSoftInput() {
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

public val View.selectableItemBackgroundResource: Int
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

public fun View.getActivity(): Activity? {
    return context.getActivity()
}

public fun Context.getActivity(): Activity? {
    if (this is Activity) {
        return this
    } else if (this is ContextWrapper) {
        return baseContext.getActivity()
    } else {
        return null
    }
}

public fun View.postDelayed(milliseconds: Long, action: () -> Unit) {
    postDelayed({ action() }, milliseconds)
}

public inline fun <T : View> ViewGroup.addView(view: T, setup: T.() -> Unit): T {
    view.setup();
    addView(view)
    return view
}

public inline fun <reified T : View> ViewGroup.addView(setup: T.() -> Unit): T {
    val view = T::class.java.getConstructor(Context::class.java).newInstance(context)
    view.setup();
    addView(view)
    return view
}

public fun EditText.onDone(action: (text: String) -> Unit) {
    imeOptions = EditorInfo.IME_ACTION_DONE
    setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
        if ((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            action(text.toString())
            return@OnKeyListener true;
        }
        false
    })
    setOnEditorActionListener({ v, actionId, event ->
        action(text.toString())
        true;
    })
}

public fun EditText.onSend(action: (text: String) -> Unit) {
    imeOptions = EditorInfo.IME_ACTION_SEND
    setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
        if ((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            action(text.toString())
            return@OnKeyListener true;
        }
        false
    })
    setOnEditorActionListener({ v, actionId, event ->
        action(text.toString())
        true;
    })
}

fun View.onChildrenRecursive(action: (View) -> Unit) {
    action(this)
    if (this is ViewGroup) {
        for (index in 0..this.childCount - 1) {
            getChildAt(index)?.onChildrenRecursive(action)
        }
    }
}

var TextView.html: String get() = throw IllegalAccessException()
    set(value){
        val newVal = value
                .replace("<li>", "<p>&bull; ")
                .replace("</li>", "</p>")
                .replace("<ul>", "")
                .replace("</ul>", "")
        text = Html.fromHtml(newVal)
    }

private val cachedPoint: Point = Point()
public val View.screenSize: Point get() {
    context.windowManager.defaultDisplay.getSize(cachedPoint)
    return cachedPoint
}
public val View.parentView: View get() {
    return parent as? View ?: throw IllegalStateException("Parent is not a ViewGroup!")
}

public fun <T, A : Adapter> AdapterView<A>.setAdapter(adapter: A, onClickAction: (T) -> Unit) {
    this.adapter = adapter
    this.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> onClickAction(adapter.getItem(position) as T) }
}

public fun WebView.javascript(function:String, vararg arguments:Any?){
    val stringArguments = arguments.map{
        when(it){
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
        loadUrl("javascript:"+call);
    }
}

fun verticalLayout(context: Context, setup: _LinearLayout.() -> Unit): _LinearLayout {
    return _LinearLayout(context).apply {
        orientation = vertical
        setup()
    }
}

fun linearLayout(context: Context, setup: _LinearLayout.() -> Unit): _LinearLayout {
    val layout = _LinearLayout(context)
    layout.setup()
    return layout
}

fun frameLayout(context: Context, setup: _FrameLayout.() -> Unit): _FrameLayout {
    val layout = _FrameLayout(context)
    layout.setup()
    return layout
}

fun relativeLayout(context: Context, setup: _RelativeLayout.() -> Unit): _RelativeLayout {
    val layout = _RelativeLayout(context)
    layout.setup()
    return layout
}

//////////////////////////////DEPRECATED///////////////////////////////

@Deprecated("This function is unnecessary abstraction and is therefore deprecated.",
        ReplaceWith(
                "_LinearLayout(@property).run{ orientation = LinearLayout.VERTICAL; init() }",
                "org.jetbrains.anko._LinearLayout"
        )
)
public inline fun ViewController.makeLinearLayout(context: Context, init: _LinearLayout.() -> Unit): LinearLayout {
    val layout = _LinearLayout(context);
    layout.orientation = LinearLayout.VERTICAL
    layout.init();
    return layout;
}

@Deprecated("This function is unnecessary abstraction and is therefore deprecated.",
        ReplaceWith(
                "_FrameLayout(context).run{ init() }",
                "org.jetbrains.anko._FrameLayout"
        )
)
public inline fun ViewController.makeFrameLayout(context: Context, init: _FrameLayout.() -> Unit): FrameLayout {
    val layout = _FrameLayout(context);
    layout.init();
    return layout;
}

@Deprecated("This function is unnecessary abstraction and is therefore deprecated.",
        ReplaceWith(
                "_RelativeLayout(context).run{ init() }",
                "org.jetbrains.anko._RelativeLayout"
        )
)
public inline fun ViewController.makeRelativeLayout(context: Context, init: _RelativeLayout.() -> Unit): RelativeLayout {
    val layout = _RelativeLayout(context);
    layout.init();
    return layout;
}

@Deprecated("This function is unnecessary abstraction and is therefore deprecated.",
        ReplaceWith(
                "ScrollView(context).run{addView(content)}",
                "android.widget.ScrollView"
        )
)
public fun ViewController.makeScrollView(context: Context, content: View): ScrollView {
    val layout = ScrollView(context)
    layout.addView(content)
    return layout
}