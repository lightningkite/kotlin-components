package com.lightningkite.kotlincomponents

import com.google.gson.*
import java.util.*

/**
 * Created by jivie on 8/13/15.
 */

object MyGson {

    private val hierarchyAdapters = HashMap<Class<*>, Any>()
    fun registerHierarchy(type: Class<*>, adapter: Any) {
        hierarchyAdapters[type] = adapter
        update()
    }

    inline fun <reified T : Any> registerHierarchy(adapter: JsonDeserializer<T>) = registerHierarchy(T::class.java, adapter)
    inline fun <reified T : Any> registerHierarchy(adapter: JsonSerializer<T>) = registerHierarchy(T::class.java, adapter)
    inline fun <reified T : Any> registerHierarchy(adapter: TypeAdapter<T>) = registerHierarchy(T::class.java, adapter)

    private val adapters = HashMap<Class<*>, Any>()
    fun register(type: Class<*>, adapter: Any) {
        adapters[type] = adapter
        update()
    }

    inline fun <reified T : Any> register(adapter: JsonDeserializer<T>) = register(T::class.java, adapter)
    inline fun <reified T : Any> register(adapter: JsonSerializer<T>) = register(T::class.java, adapter)
    inline fun <reified T : Any> register(adapter: TypeAdapter<T>) = register(T::class.java, adapter)

    val json: JsonParser = JsonParser()

    private var gsonInternal: Gson? = null
    val gson: Gson get() = gsonInternal ?: initialize()

    fun update() {
        if (gsonInternal == null) return;
        initialize()
    }

    fun initialize(): Gson {
        val builder = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        for ((type, adapter) in hierarchyAdapters) {
            builder.registerTypeHierarchyAdapter(type, adapter)
        }
        for ((type, adapter) in adapters) {
            builder.registerTypeAdapter(type, adapter)
        }
        val gson = builder.create()
        gsonInternal = gson
        return gson
    }

}

fun <E> Collection<E>.toJsonArray(): JsonArray {
    val array = JsonArray()
    for (value in this)
        array.add(value.toJsonElement())
    return array;
}

fun Any?.toJsonElement(): JsonElement {
    if (this == null)
        return JsonNull.INSTANCE

    return when (this) {
        is Number -> JsonPrimitive(this)
        is Char -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is JsonElement -> this
        else -> throw IllegalArgumentException("${this} cannot be converted to JSON")
    }
}

fun Any.gsonTo(gson: Gson = MyGson.gson): String {
    return gson.toJson(this)
}

inline fun <reified T : Any> String.gsonFrom(gson: Gson = MyGson.gson): T? {
    return gson.fromJson(this, T::class.java)
}

inline fun <reified T : Any> JsonElement.gsonFrom(gson: Gson = MyGson.gson): T? {
    return gson.fromJson(this, T::class.java)
}


inline fun <reified T : Any> String.gsonFromType(type: Class<T>, gson: Gson = MyGson.gson): T? {
    return gson.fromJson(this, type)
}

inline fun <reified T : Any> JsonElement.gsonFromType(type: Class<T>, gson: Gson = MyGson.gson): T? {
    return gson.fromJson(this, type)
}
