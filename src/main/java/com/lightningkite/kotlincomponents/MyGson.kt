package com.lightningkite.kotlincomponents

import com.google.gson.*
import com.lightningkite.kotlincomponents.observable.KObservable
import java.lang.reflect.ParameterizedType
import java.util.*

/**
 * Created by jivie on 8/13/15.
 */

object MyGson {

    private val hierarchyAdapters = ArrayList<Pair<Class<*>, Any>>()
    fun registerHierarchy(type: Class<*>, adapter: Any) {
        hierarchyAdapters += type to adapter
        update()
    }

    inline fun <reified T : Any> registerHierarchy(adapter: JsonDeserializer<T>) = registerHierarchy(T::class.java, adapter)
    inline fun <reified T : Any> registerHierarchy(adapter: JsonSerializer<T>) = registerHierarchy(T::class.java, adapter)
    inline fun <reified T : Any> registerHierarchy(adapter: TypeAdapter<T>) = registerHierarchy(T::class.java, adapter)

    private val adapters = ArrayList<Pair<Class<*>, Any>>()
    fun register(type: Class<*>, adapter: Any) {
        adapters += type to adapter
        update()
    }

    inline fun <reified T : Any> register(adapter: JsonDeserializer<T>) = register(T::class.java, adapter)
    inline fun <reified T : Any> register(adapter: JsonSerializer<T>) = register(T::class.java, adapter)
    inline fun <reified T : Any> register(adapter: TypeAdapter<T>) = register(T::class.java, adapter)

    private val factories = ArrayList<TypeAdapterFactory>()
    fun registerFactory(factory: TypeAdapterFactory) {
        factories.add(factory)
        update()
    }

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
        for (factory in factories) {
            builder.registerTypeAdapterFactory(factory)
        }
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
        register<KObservable<*>>(JsonSerializer { t, type, context ->
            context.serialize(t.value)
        })
        register<KObservable<*>>(JsonDeserializer { element, type, context ->
            if (type !is ParameterizedType) throw IllegalArgumentException()
            val innerType = type.actualTypeArguments[0]
            KObservable<Any>(context.deserialize(element, innerType)) as KObservable<*>
        })
    }
}
