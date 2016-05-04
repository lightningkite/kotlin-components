package com.lightningkite.kotlincomponents

import android.content.Context
import org.jetbrains.anko.connectivityManager

/**
 * Created by jivie on 4/5/16.
 */
inline fun Context.isNetworkAvailable(): Boolean = connectivityManager.activeNetworkInfo?.isConnected ?: false