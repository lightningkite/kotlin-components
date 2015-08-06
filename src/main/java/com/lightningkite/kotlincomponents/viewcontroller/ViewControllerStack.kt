package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Intent
import android.os.Bundle

public interface ViewControllerStack {
    public fun pushView(newController: ViewController, onResult: (result: Any?) -> Unit): Unit
    public fun pushView(newController: ViewController): Unit = pushView(newController, {})
    public fun popView()
    public fun resetView(newController: ViewController)
    public fun replaceView(newController: ViewController)
    public fun startIntent(intent: Intent, onResult: (result: Int, data: Intent?) -> Unit, options: Bundle)
    public fun startIntent(intent: Intent, onResult: (result: Int, data: Intent?) -> Unit = { resultCode, data -> }) {
        startIntent(intent, onResult, Bundle.EMPTY)
    }
}

public data class ViewControllerData(val controller: ViewController, var onResult: (result: Any?) -> Unit = {}) : ViewController by controller