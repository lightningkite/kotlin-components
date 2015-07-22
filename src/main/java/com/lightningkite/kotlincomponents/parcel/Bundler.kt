package com.lightningkite.kotlincomponents.parcel

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import com.lightningkite.kotlincomponents.databinding.Bond
import java.util.ArrayList

/**
 * Created by jivie on 6/25/15.
 */

public annotation class bundled

public object Bundler {

    public inline fun toBundle<reified T>(toBundleObj: T): Bundle {
        return toBundle(toBundleObj, javaClass<T>())
    }

    public fun toBundle(toBundleObj: Any, javaCl: Class<*>): Bundle {
        val bundle = Bundle()
        for (field in javaCl.getDeclaredFields()) {
            if (field.getAnnotation(javaClass<bundled>()) == null) continue
            val name = field.getName()
            field.setAccessible(true)
            val value: Any? = field.get(toBundleObj)
            if (value != null) {
                writeToBundle(bundle, name, value)
            }
        }
        return bundle
    }

    public inline fun fromBundle<reified T>(bundle: Bundle, fromBundleObj: T): T {
        fromBundle(bundle, fromBundleObj, javaClass<T>())
        return fromBundleObj
    }

    public fun fromBundle(bundle: Bundle, fromBundleObj: Any?, javaCl: Class<*>): Any? {
        if (fromBundleObj == null) return fromBundleObj
        for (field in javaCl.getDeclaredFields()) {
            if (field.getAnnotation(javaClass<bundled>()) == null) continue
            val name = field.getName()
            field.setAccessible(true)
            if (bundle.get(name) == null) continue
            field.set(fromBundleObj, readFromBundle(bundle, name, field.getType(), field.get(fromBundleObj)));
        }
        return fromBundleObj
    }

    public fun writeToBundle(bundle: Bundle, name: String, value: Any) {
        when (value) {
            is Int -> bundle.putInt(name, value)
            is Short -> bundle.putShort(name, value)
            is Byte -> bundle.putByte(name, value)
            is Char -> bundle.putChar(name, value)
            is Long -> bundle.putLong(name, value)
            is Boolean -> bundle.putBoolean(name, value)
            is IntArray -> bundle.putIntArray(name, value)
            is ShortArray -> bundle.putShortArray(name, value)
            is ByteArray -> bundle.putByteArray(name, value)
            is CharArray -> bundle.putCharArray(name, value)
            is LongArray -> bundle.putLongArray(name, value)
            is BooleanArray -> bundle.putBooleanArray(name, value)
            is String -> bundle.putString(name, value)
            is Array<String> -> bundle.putStringArray(name, value)
            is Parcelable -> bundle.putParcelable(name, value)
            is Array<Parcelable> -> bundle.putParcelableArray(name, value)
            is ArrayList<*> -> {
                if (value.size() > 0) {
                    val subvalue = value.get(0)
                    if (subvalue != null) {
                        when (subvalue) {
                            is Int -> bundle.putIntegerArrayList(name, value as ArrayList<Int>?)
                            is String -> bundle.putStringArrayList(name, value as ArrayList<String>?)
                            is Parcelable -> bundle.putParcelableArrayList(name, value as ArrayList<out Parcelable>?)
                        }
                    }
                }
            }
            is Bond<*> -> value.writeToBundle(bundle, name)
            else -> {
                try {
                    bundle.putParcelable(name, toBundle(value, value.javaClass))
                } catch(e: Exception) {
                    //squish
                }
            }
        }
    }

    public fun readFromBundle(bundle: Bundle, name: String, type: Class<*>, inputObj: Any?): Any? {
        when (type) {
            javaClass<Int>() -> return (bundle.getInt(name))
            javaClass<Short>() -> return (bundle.getShort(name))
            javaClass<Byte>() -> return (bundle.getByte(name))
            javaClass<Char>() -> return (bundle.getChar(name))
            javaClass<Long>() -> return (bundle.getLong(name))
            javaClass<Boolean>() -> return (bundle.getBoolean(name))

            javaClass<java.lang.Integer>() -> return (bundle.getInt(name))
            javaClass<java.lang.Short>() -> return (bundle.getShort(name))
            javaClass<java.lang.Byte>() -> return (bundle.getByte(name))
            javaClass<java.lang.Character>() -> return (bundle.getChar(name))
            javaClass<java.lang.Long>() -> return (bundle.getLong(name))
            javaClass<java.lang.Boolean>() -> return (bundle.getBoolean(name))

            javaClass<IntArray>() -> return (bundle.getIntArray(name))
            javaClass<ShortArray>() -> return (bundle.getShortArray(name))
            javaClass<ByteArray>() -> return (bundle.getByteArray(name))
            javaClass<CharArray>() -> return (bundle.getCharArray(name))
            javaClass<LongArray>() -> return (bundle.getLongArray(name))
            javaClass<BooleanArray>() -> return (bundle.getBooleanArray(name))

            javaClass<String>() -> return (bundle.getString(name))
            javaClass<Array<String>>() -> return (bundle.getStringArray(name))
            javaClass<Parcelable>() -> return (bundle.getParcelable(name))
            javaClass<Array<Parcelable>>() -> return (bundle.getParcelableArray(name))

            javaClass<Bond<*>>() -> {
                (inputObj as Bond<*>).loadFromBundle(bundle, name)
                return inputObj
            }
            javaClass<ArrayList<*>>() -> {
                try {
                    return (bundle.getIntegerArrayList(name))
                } catch(e: Exception) {
                    try {
                        return (bundle.getStringArrayList(name))
                    } catch(e: Exception) {
                        try {
                            return (bundle.getParcelableArrayList<Parcelable>(name))
                        } catch(e: Exception) {

                        }
                    }
                }
            }
            else -> {
                try {
                    val result = fromBundle(
                            bundle.getParcelable<Bundle>(name),
                            type.getConstructor().newInstance(),
                            type
                    )
                    return result
                } catch(e: Exception) {
                    Log.e("Parcelabler", "Unrecognized class: " + type.toString())
                    e.printStackTrace()
                    return null
                }
            }
        }
        return null
    }

}