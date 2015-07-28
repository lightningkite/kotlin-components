package com.lightningkite.kotlincomponents.viewcontroller

import android.app.Activity
import android.os.Bundle
import java.util.HashSet

/**
 * Created by jivie on 6/25/15.
 */
public open class LifecycleActivity : Activity() {

    private val lifecycleListeners: HashSet<ActivityLifecycleListener> = HashSet()
    public fun addLifecycleListener(listener: ActivityLifecycleListener) {
        lifecycleListeners.add(listener)
    }

    public fun removeLifecycleListener(listener: ActivityLifecycleListener) {
        lifecycleListeners.remove(listener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (listener in lifecycleListeners) {
            listener.onCreate(savedInstanceState)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        for (listener in lifecycleListeners) {
            listener.onRestoreInstanceState(savedInstanceState)
        }
    }

    override fun onResume() {
        super.onResume()
        for (listener in lifecycleListeners) {
            listener.onResume()
        }
    }

    override fun onPause() {
        for (listener in lifecycleListeners) {
            listener.onPause()
        }
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        for (listener in lifecycleListeners) {
            listener.onSaveInstanceState(outState)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        for (listener in lifecycleListeners) {
            listener.onLowMemory()
        }
        super.onLowMemory()
    }

    override fun onDestroy() {
        for (listener in lifecycleListeners) {
            listener.onDestroy()
        }
        super.onDestroy()
    }
}
