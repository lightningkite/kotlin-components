package com.lightningkite.kotlincomponents

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.toJson
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull

/**
 * Created by jivie on 8/13/15.
 */

public object BasicGson {
    public val gson: Gson = Gson()
}

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

public fun Any.gsonTo(gson: Gson = BasicGson.gson): String {
    return gson.toJson(this)
}

public inline fun <reified T : Any> String.gsonFrom(gson: Gson = BasicGson.gson): T? {
    return gson.fromJson<T>(this)
}
