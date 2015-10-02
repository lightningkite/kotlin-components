package com.lightningkite.kotlincomponents.viewcontroller

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.lightningkite.kotlincomponents.animation.AnimationSet

/**
 * Created by jivie on 6/26/15.
 */
public abstract class ViewControllerActivity() : Activity(), ViewControllerStack, ViewControllerView.IntentListener {

    public companion object {
        public val REQUEST_CODE: Int = 127891
    }

    lateinit var viewControllerView: ViewControllerView
    public abstract val startViewController: ViewController
    public open val animationSetPush: AnimationSet? get() = AnimationSet.slidePush
    public open val animationSetPop: AnimationSet? get() = AnimationSet.slidePop

    override fun pushView(newController: ViewController, onResult: (Any?) -> Unit) = viewControllerView.pushView(newController, onResult)
    override fun pushView(newController: ViewController) = viewControllerView.pushView(newController)
    override fun popView(): Boolean = viewControllerView.popView()
    override fun resetView(newController: ViewController) = viewControllerView.resetView(newController)
    override fun replaceView(newController: ViewController) = viewControllerView.replaceView(newController)

    override fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit, options: Bundle) = viewControllerView.startIntent(intent, onResult, options)
    override fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit) = viewControllerView.startIntent(intent, onResult)

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
        viewControllerView = ViewControllerView(this, startViewController, this, "activity", animationSetPush, animationSetPop)
        setContentView(viewControllerView)
    }

    override fun onBackPressed() {
        if (viewControllerView.stack.size() > 1) {
            viewControllerView.popView()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        viewControllerView.dispose()
        super.onDestroy()
    }
}