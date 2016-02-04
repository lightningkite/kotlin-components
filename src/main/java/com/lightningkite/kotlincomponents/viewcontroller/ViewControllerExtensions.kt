package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.lightningkite.kotlincomponents.vertical
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import org.jetbrains.anko._FrameLayout
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko._RelativeLayout

/**
 * Various extension functions specifically for ViewControllers.
 * Created by josep on 11/6/2015.
 */

fun ViewController.verticalLayout(activity: VCActivity, setup: _LinearLayout.() -> Unit): _LinearLayout {
    return _LinearLayout(activity).apply {
        orientation = vertical
        setup()
    }
}

fun ViewController.linearLayout(activity: VCActivity, setup: _LinearLayout.() -> Unit): _LinearLayout {
    val layout = _LinearLayout(activity)
    layout.setup()
    return layout
}

fun ViewController.frameLayout(activity: VCActivity, setup: _FrameLayout.() -> Unit): _FrameLayout {
    val layout = _FrameLayout(activity)
    layout.setup()
    return layout
}

fun ViewController.relativeLayout(activity: VCActivity, setup: _RelativeLayout.() -> Unit): _RelativeLayout {
    val layout = _RelativeLayout(activity)
    layout.setup()
    return layout
}

fun ViewController.inflate(context: Context, layoutResource: Int, init: View.() -> Unit): View {
    val layout = LayoutInflater.from(context).inflate(layoutResource, null);
    layout.init();
    return layout;
}