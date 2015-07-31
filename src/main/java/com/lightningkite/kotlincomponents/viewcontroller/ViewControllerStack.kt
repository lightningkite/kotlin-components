package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Intent
import android.os.Bundle

public interface ViewControllerStack {
    public fun pushView(newController: ViewController, onResult: (result: Any?) -> Unit = {})
    public fun popView()
    public fun startIntent(intent: Intent, onResult: (result: Int, data: Intent?) -> Unit = { a, b -> }, options: Bundle = Bundle.EMPTY)
}

public data class ViewControllerData(val controller: ViewController, var onResult: (result: Any?) -> Unit = {}) : ViewController by controller