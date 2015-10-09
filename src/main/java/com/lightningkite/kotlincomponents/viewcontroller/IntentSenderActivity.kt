package com.lightningkite.kotlincomponents.viewcontroller

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import java.util.*

/**
 * Created by jivie on 10/9/15.
 */
open class IntentSenderActivity : Activity(), IntentSender {
    public companion object {
        public val returns: HashMap<Int, (Int, Intent?) -> Unit> = HashMap()
    }

    override fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit, options: Bundle) {
        val generated: Int = (Math.random() * Int.MAX_VALUE).toInt()
        returns[generated] = onResult
        startActivityForResult(intent, generated, options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            returns[requestCode]?.invoke(resultCode, data)
            returns.remove(requestCode)
        }
    }
}