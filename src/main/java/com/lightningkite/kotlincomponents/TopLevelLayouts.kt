package com.lightningkite.kotlincomponents

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import org.jetbrains.anko._FrameLayout
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko._RelativeLayout

/**
 * Created by josep on 3/3/2016.
 */


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

fun inflate(context: Context, layoutResource: Int, init: View.() -> Unit): View {
    val layout = LayoutInflater.from(context).inflate(layoutResource, null);
    layout.init();
    return layout;
}