package com.lightningkite.kotlincomponents.device

import android.content.Context
import org.jetbrains.anko.defaultSharedPreferences
import java.util.*

/**
 * Created by jivie on 5/20/16.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Context.getUniquePreferenceId(): String {
    val key = "com.lightningkite.kotlincomponents.device.install_uuid"
    val found: String? = defaultSharedPreferences.getString(key, null)
    if (found != null) return found
    val made = UUID.randomUUID().toString()
    defaultSharedPreferences.edit().putString(key, made).apply()
    return made
}