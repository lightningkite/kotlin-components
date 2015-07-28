package com.lightningkite.kotlincomponents.viewcontroller

import android.os.Bundle

/**
 * Created by jivie on 6/26/15.
 */
public interface ActivityLifecycleListener {
    public fun onCreate(savedInstanceState: Bundle?) {
    }

    public fun onRestoreInstanceState(savedInstanceState: Bundle) {
    }

    public fun onResume() {
    }

    public fun onPause() {
    }

    public fun onSaveInstanceState(outState: Bundle) {
    }

    public fun onLowMemory() {
    }

    public fun onDestroy() {
    }
}