package com.lightningkite.kotlincomponents

import com.github.salomonbrys.kotson.toJson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull

/**
 * Created by jivie on 8/13/15.
 */

public fun <E> Collection<E>.toJsonArray(): JsonArray {
    val array = JsonArray()
    for (value in this)
        array.add(value.toJsonElement())
    return array;
}

public fun Any?.toJsonElement(): JsonElement {
    if (this == null)
        return JsonNull.INSTANCE

    return when (this) {
        is Number -> this.toJson()
        is Char -> this.toJson()
        is Boolean -> this.toJson()
        is String -> this.toJson()
        is JsonElement -> this
        else -> throw IllegalArgumentException("${this} cannot be converted to JSON")
    }
}