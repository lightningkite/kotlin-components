package com.lightningkite.kotlincomponents.networking

/**
 * Created by jivie on 2/18/16.
 */
inline fun String.urlAddQueryParameter(key: String): String {
    if (this.contains('?')) {
        return this + "&" + key
    } else {
        return this + "?" + key
    }
}

inline fun String.urlAddQueryParameter(key: String, value: String): String {
    if (this.contains('?')) {
        return this + "&" + key + "=" + value
    } else {
        return this + "?" + key + "=" + value
    }
}

inline fun String.urlAddOptionalQueryParameter(key: String, value: String?): String {
    if (value == null) return this
    if (this.contains('?')) {
        return this + "&" + key + "=" + value
    } else {
        return this + "?" + key + "=" + value
    }
}