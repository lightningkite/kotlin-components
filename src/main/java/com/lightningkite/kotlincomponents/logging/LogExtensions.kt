package com.lightningkite.kotlincomponents.logging

import android.util.Log

/**
 * Functions for doing some quick logging.
 * Created by josep on 10/4/2015.
 */
fun Any.logD(text: String) = Log.d(javaClass.simpleName, text)

fun Any.logI(text: String) = Log.i(javaClass.simpleName, text)
fun Any.logW(text: String) = Log.w(javaClass.simpleName, text)
fun Any.logE(text: String) = Log.e(javaClass.simpleName, text)
fun Any.logWTF(text: String) = Log.wtf(javaClass.simpleName, text)

fun Any.logD(obj: Any?) = Log.d(javaClass.simpleName, obj.toString())
fun Any.logI(obj: Any?) = Log.i(javaClass.simpleName, obj.toString())
fun Any.logW(obj: Any?) = Log.w(javaClass.simpleName, obj.toString())
fun Any.logE(obj: Any?) = Log.e(javaClass.simpleName, obj.toString())
fun Any.logWTF(obj: Any?) = Log.wtf(javaClass.simpleName, obj.toString())

private fun buildDisplayList(vararg objs: Any?): String {
    val builder = StringBuilder()
    for (obj in objs) {
        builder.append(obj.toString())
        builder.append(", ")
    }
    if (builder.length > 2) {
        builder.setLength(builder.length - 2)
    }
    return builder.toString()
}

fun Any.logD(vararg objs: Any?) = Log.d(javaClass.simpleName, buildDisplayList(objs))
fun Any.logI(vararg objs: Any?) = Log.i(javaClass.simpleName, buildDisplayList(objs))
fun Any.logW(vararg objs: Any?) = Log.w(javaClass.simpleName, buildDisplayList(objs))
fun Any.logE(vararg objs: Any?) = Log.e(javaClass.simpleName, buildDisplayList(objs))
fun Any.logWTF(vararg objs: Any?) = Log.wtf(javaClass.simpleName, buildDisplayList(objs))