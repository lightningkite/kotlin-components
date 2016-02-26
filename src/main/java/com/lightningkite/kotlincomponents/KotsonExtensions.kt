package com.lightningkite.kotlincomponents

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
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

    init {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        register(JsonSerializer<Date> { item, type, jsonSerializationContext ->
            JsonPrimitive(format.format(item) + "+00:00")
        })
        register(JsonDeserializer { jsonElement, type, jsonDeserializationContext ->
            if (jsonElement !is JsonPrimitive) throw IllegalArgumentException()
            if (!jsonElement.isString) throw IllegalArgumentException()
            val str = jsonElement.asString
            val result = format.parse(str.substring(0, str.length - 6))
            println(result)
            result
        })
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
    try {
        return gson.fromJson<T>(this)
    } catch(e: JsonParseException) {
        e.printStackTrace()
    } catch(e: JsonSyntaxException) {
        e.printStackTrace()
    }
    return null
}

inline fun <reified T : Any> JsonElement.gsonFrom(gson: Gson = MyGson.gson): T? {
    try {
        return gson.fromJson<T>(this)
    } catch(e: JsonParseException) {
        e.printStackTrace()
    } catch(e: JsonSyntaxException) {
        e.printStackTrace()
    }
    return null
}

inline fun <T : Any> String.gsonFrom(type: Class<T>, gson: Gson = MyGson.gson): T? {
    try {
        return gson.fromJson(this, type)
    } catch(e: JsonParseException) {
        e.printStackTrace()
    } catch(e: JsonSyntaxException) {
        e.printStackTrace()
    }
    return null
}

inline fun <T : Any> JsonElement.gsonFrom(type: Class<T>, gson: Gson = MyGson.gson): T? {
    try {
        return gson.fromJson(this, type)
    } catch(e: JsonParseException) {
        e.printStackTrace()
    } catch(e: JsonSyntaxException) {
        e.printStackTrace()
    }
    return null
}

inline fun <T : Any> String.gsonFrom(type: Type, gson: Gson = MyGson.gson): T? {
    try {
        return gson.fromJson<T>(this, type)
    } catch(e: JsonParseException) {
        e.printStackTrace()
    } catch(e: JsonSyntaxException) {
        e.printStackTrace()
    }
    return null
}

inline fun <T : Any> JsonElement.gsonFrom(type: Type, gson: Gson = MyGson.gson): T? {
    try {
        return gson.fromJson<T>(this, type)
    } catch(e: JsonParseException) {
        e.printStackTrace()
    } catch(e: JsonSyntaxException) {
        e.printStackTrace()
    }
    return null
}
