package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Intent
import android.os.Bundle

/**
 * Created by jivie on 10/9/15.
 */
interface IntentSender {
    object dummy : IntentSender {
        override fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit, options: Bundle) {
            throw UnsupportedOperationException()
        }
    }

    fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit, options: Bundle)
}