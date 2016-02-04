package com.github.salomonbrys.kotson

import com.google.gson.*

fun Number.toJson(): JsonPrimitive = JsonPrimitive(this)

fun Char.toJson(): JsonPrimitive = JsonPrimitive(this)

fun Boolean.toJson(): JsonPrimitive = JsonPrimitive(this)

fun String.toJson(): JsonPrimitive = JsonPrimitive(this)

internal fun Any?.toJsonElement(): JsonElement {
    if (this == null)
        return jsonNull

    return when (this) {
        is Number -> toJson()
        is Char -> toJson()
        is Boolean -> toJson()
        is String -> toJson()
        is JsonElement -> this
        else -> throw IllegalArgumentException("${this} cannot be converted to JSON")
    }
}

fun jsonArray(vararg values: Any?): JsonArray {
    val array = JsonArray()
    for (value in values)
        array.add(value.toJsonElement())
    return array;
}

fun jsonObject(vararg values: Pair<String, Any?>): JsonObject {
    val obj = JsonObject()
    for ((key, value) in values) {
        obj.add(key, value.toJsonElement())
    }
    return obj;
}

fun JsonObject.shallowCopy(): JsonObject = JsonObject().apply { this@shallowCopy.entrySet().forEach { put(it) } }
fun JsonArray.shallowCopy(): JsonArray = JsonArray().apply { addAll(this@shallowCopy) }

private fun JsonElement._deepCopy(): JsonElement {
    return when (this) {
        is JsonNull, is JsonPrimitive -> this
        is JsonObject -> deepCopy()
        is JsonArray -> deepCopy()
        else -> throw IllegalArgumentException("Unknown JsonElement: ${this}")
    }
}

fun JsonObject.deepCopy(): JsonObject = JsonObject().apply { this@deepCopy.entrySet().forEach { add(it.key, it.value._deepCopy()) } }

fun JsonArray.deepCopy(): JsonArray = JsonArray().apply { this@deepCopy.forEach { add(it._deepCopy()) } }
