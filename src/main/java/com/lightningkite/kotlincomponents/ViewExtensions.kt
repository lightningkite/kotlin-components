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
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import org.jetbrains.anko.*

/**
 * Created by jivie on 7/16/15.
 */

public fun ViewController.inflate(context: Context, @LayoutRes layoutResource: Int, init: View.() -> Unit): View {
    val layout = LayoutInflater.from(context).inflate(layoutResource, null);
    layout.init();
    return layout;
}

public fun View.dip(value: Int): Int = getContext().dip(value)
public val View.context: Context get() = getContext()

public fun View.animateHighlight(milliseconds: Long, color: Int, millisecondsTransition: Int = 200) {
    assert(milliseconds > millisecondsTransition * 2, "The time shown must be at least twice as much as the transition time")
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

public @ColorRes var TextView.textColorResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        setTextColor(resources.getColor(value))
    }
public @ColorRes var TextView.hintTextColorResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        setHintTextColor(resources.getColor(value))
    }

public fun TextView.setFont(context: Context, fileName: String) {
    val font = Typeface.createFromAsset(context.assets, fileName)
    typeface = font
}

public @DrawableRes val View.selectableItemBackground: Int
    get() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            val outValue = TypedValue();
            getContext().theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true);
            return outValue.resourceId
        }
        return 0
    }

public fun View.getActivity(): Activity? {
    return getContext().getActivity()
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
    postDelayed(object : Runnable {
        override fun run() = action()
    }, milliseconds)
}

public inline fun <T : View> ViewGroup.addView(view: T, setup: T.() -> Unit): T {
    view.setup();
    addView(view)
    return view
}

public inline fun <reified T : View> ViewGroup.addView(setup: T.() -> Unit): T {
    val view = T::class.java.getConstructor(Context::class.java).newInstance(getContext())
    view.setup();
    addView(view)
    return view
}

public fun EditText.onDone(action: (text: String) -> Unit) {
    imeOptions = EditorInfo.IME_ACTION_DONE
    setOnKeyListener(object : View.OnKeyListener {
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if ((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                action(text.toString())
                return true;
            }
            return false
        }
    })
    setOnEditorActionListener(object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            action(text.toString())
            return true;
        }
    })
}

private val cachedPoint: Point = Point()
public val View.screenSize: Point get() {
    getContext().windowManager.defaultDisplay.getSize(cachedPoint)
    return cachedPoint
}
public val View.parentView: View get() {
    return parent as? View ?: throw IllegalStateException("Parent is not a ViewGroup!")
}

public fun <T, A : Adapter> AdapterView<A>.setAdapter(adapter: A, onClickAction: (T) -> Unit) {
    this.adapter = adapter
    this.onItemClickListener = object : AdapterView.OnItemClickListener {
        @Suppress("UNCHECKED_CAST")
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            onClickAction(adapter.getItem(position) as T)
        }
    }
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