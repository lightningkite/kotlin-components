package com.lightningkite.kotlincomponents

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View

/**
 * Created by jivie on 7/22/15.
 */
public fun View.getActivity(): Activity? {
    return getContext().getActivity()
}

public fun Context.getActivity(): Activity? {
    if (this is Activity) {
        return this
    } else if (this is ContextWrapper) {
        return getBaseContext().getActivity()
    } else {
        return null
    }
}