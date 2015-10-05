package com.lightningkite.kotlincomponents.logging

import android.util.Log

/**
 * Created by josep on 10/4/2015.
 */
public fun Any.logD(text: String) = Log.d(javaClass.simpleName, text)

public fun Any.logI(text: String) = Log.i(javaClass.simpleName, text)
public fun Any.logW(text: String) = Log.w(javaClass.simpleName, text)
public fun Any.logE(text: String) = Log.e(javaClass.simpleName, text)
public fun Any.logWTF(text: String) = Log.wtf(javaClass.simpleName, text)

public fun Any.logD(obj: Any?) = Log.d(javaClass.simpleName, obj.toString())
public fun Any.logI(obj: Any?) = Log.i(javaClass.simpleName, obj.toString())
public fun Any.logW(obj: Any?) = Log.w(javaClass.simpleName, obj.toString())
public fun Any.logE(obj: Any?) = Log.e(javaClass.simpleName, obj.toString())
public fun Any.logWTF(obj: Any?) = Log.wtf(javaClass.simpleName, obj.toString())