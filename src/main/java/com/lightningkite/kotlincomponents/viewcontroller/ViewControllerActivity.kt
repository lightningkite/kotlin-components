package com.lightningkite.kotlincomponents.viewcontroller

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.lightningkite.kotlincomponents.animation.AnimationSet

/**
 * Created by jivie on 6/26/15.
 */
public abstract class ViewControllerActivity() : Activity(), ViewControllerView.IntentListener {

    public companion object {
        public val REQUEST_CODE: Int = 127891
    }

    lateinit var viewControllerView: ViewControllerView
    public abstract val startViewController: ViewController
    public open val animationSetPush: AnimationSet? get() = AnimationSet.slidePush
    public open val animationSetPop: AnimationSet? get() = AnimationSet.slidePop
    public open val tag: String get() = "activity"

    override fun startActivityForResult(tag: String, intent: Intent, options: Bundle) {
        startActivityForResult(intent, REQUEST_CODE, options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            viewControllerView.onActivityResult(resultCode, data)
        }
    }

    public open fun setupBeforeCreate() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBeforeCreate()
        viewControllerView = object : ViewControllerView(this, startViewController, this, tag, animationSetPush, animationSetPop) {
            override fun popView(): Boolean {
                val result = super.popView()
                if (result == false) finish()
                return result
            }
        }
        setContentView(viewControllerView)
    }

    override fun onBackPressed() {
        viewControllerView.popView()
    }

    override fun onDestroy() {
        viewControllerView.dispose()
        super.onDestroy()
    }
}