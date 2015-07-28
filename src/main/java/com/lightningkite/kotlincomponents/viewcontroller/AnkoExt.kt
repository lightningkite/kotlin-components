package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Context
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout

/**
 * Created by jivie on 7/16/15.
 */
public fun ViewController.makeLinearLayout(context: Context, init:LinearLayout.()->Unit):LinearLayout{
    val layout = LinearLayout(context);
    layout.init();
    return layout;
}
public fun ViewController.makeFrameLayout(context: Context, init:FrameLayout.()->Unit):FrameLayout{
    val layout = FrameLayout(context);
    layout.init();
    return layout;
}
public fun ViewController.makeRelativeLayout(context: Context, init:RelativeLayout.()->Unit):RelativeLayout{
    val layout = RelativeLayout(context);
    layout.init();
    return layout;
}