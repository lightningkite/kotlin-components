package com.lightningkite.kotlincomponents.parcel

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import java.util.*

/**
 * Various pieces to change normal objects into bundles.
 * Created by jivie on 6/25/15.
 */

public annotation class bundled

/**
 * Use at your own risk, as bundling arrays and bonds is broken due to new versions of Kotlin.
 * You probably shouldn't need this class if you are using view controllers correctly, as you can
 * pass data between them directly.
 */
public object Bundler {

    public inline fun <reified T : Any> toBundle(toBundleObj: T): Bundle {
        return toBundle(toBundleObj, T::class.java)
    }

    public fun toBundle(toBundleObj: Any, javaCl: Class<*>): Bundle {
        val bundle = Bundle()
        for (field in javaCl.declaredFields) {
            if (field.getAnnotation(bundled::class.java) == null) continue
            val name = field.name
            field.isAccessible = true
            val value: Any? = field.get(toBundleObj)
            if (value != null) {
                writeToBundle(bundle, name, value)
            }
        }
        return bundle
    }

    public inline fun <reified T : Any> fromBundle(bundle: Bundle, fromBundleObj: T): T {
        fromBundle(bundle, fromBundleObj, T::class.java)
        return fromBundleObj
    }

    public fun fromBundle(bundle: Bundle, fromBundleObj: Any?, javaCl: Class<*>): Any? {
        if (fromBundleObj == null) return fromBundleObj
        for (field in javaCl.declaredFields) {
            if (field.getAnnotation(bundled::class.java) == null) continue
            val name = field.name
            field.isAccessible = true
            if (bundle.get(name) == null) continue
            field.set(fromBundleObj, readFromBundle(bundle, name, field.type, field.get(fromBundleObj)));
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
            //is Array<String> -> bundle.putStringArray(name, value) //TODO
            is Parcelable -> bundle.putParcelable(name, value)
            //is Array<Parcelable> -> bundle.putParcelableArray(name, value)
            is ArrayList<*> -> {
                if (value.size > 0) {
                    val subvalue = value[0]
                    if (subvalue != null) {
                        when (subvalue) {
                            is Int -> bundle.putIntegerArrayList(name, value as ArrayList<Int>?)
                            is String -> bundle.putStringArrayList(name, value as ArrayList<String>?)
                            is Parcelable -> bundle.putParcelableArrayList(name, value as ArrayList<out Parcelable>?)
                        }
                    }
                }
            }
        //is Bond<*> -> value.writeToBundle(bundle, name)
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
            Int::class.java -> return (bundle.getInt(name))
            Short::class.java -> return (bundle.getShort(name))
            Byte::class.java -> return (bundle.getByte(name))
            Char::class.java -> return (bundle.getChar(name))
            Long::class.java -> return (bundle.getLong(name))
            Boolean::class.java -> return (bundle.getBoolean(name))

//            java.lang.Integer::class.java -> return (bundle.getInt(name))
//            java.lang.Short::class.java -> return (bundle.getShort(name))
//            java.lang.Byte::class.java -> return (bundle.getByte(name))
//            java.lang.Character::class.java -> return (bundle.getChar(name))
//            java.lang.Long::class.java -> return (bundle.getLong(name))
//            java.lang.Boolean::class.java -> return (bundle.getBoolean(name))

            IntArray::class.java -> return (bundle.getIntArray(name))
            ShortArray::class.java -> return (bundle.getShortArray(name))
            ByteArray::class.java -> return (bundle.getByteArray(name))
            CharArray::class.java -> return (bundle.getCharArray(name))
            LongArray::class.java -> return (bundle.getLongArray(name))
            BooleanArray::class.java -> return (bundle.getBooleanArray(name))

            String::class.java -> return (bundle.getString(name))
            Array<String>::class.java -> return (bundle.getStringArray(name))
            Parcelable::class.java -> return (bundle.getParcelable(name))
            Array<Parcelable>::class.java -> return (bundle.getParcelableArray(name))

        /*Bond::class.java -> {
            (inputObj as Bond<*>).loadFromBundle(bundle, name)
            return inputObj
        }*/
            ArrayList::class.java -> {
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