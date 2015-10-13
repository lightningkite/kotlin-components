package com.lightningkite.kotlincomponents.viewcontroller.containers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lightningkite.kotlincomponents.viewcontroller.ViewController

/**
 * Created by jivie on 10/12/15.
 */
interface VCContext {
    fun startIntent(intent: Intent, onResult: (Int, Intent?) -> Unit, options: Bundle)
    val context: Context
}