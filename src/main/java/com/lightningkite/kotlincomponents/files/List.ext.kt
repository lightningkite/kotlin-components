package com.lightningkite.kotlincomponents.files

import android.content.Context
import com.lightningkite.kotlincomponents.gsonFrom
import com.lightningkite.kotlincomponents.gsonToOptional
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.reflect.Type

/**
 * Created by jivie on 3/29/16.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <E> List<E>.save(file: File) {
    FileOutputStream(file).bufferedWriter().use {
        for (item in this) {
            it.appendln(item.gsonToOptional())
        }
    }
}

inline fun <reified E : Any> MutableList<E>.load(file: File) {
    FileInputStream(file).bufferedReader().use {
        for (line in it.lineSequence()) {
            val item = line.gsonFrom<E>()
            if (item != null) {
                this.add(item)
            }
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <E : Any> MutableList<E>.load(file: File, type: Type) {
    FileInputStream(file).bufferedReader().use {
        for (line in it.lineSequence()) {
            val item = line.gsonFrom<E>(type)
            if (item != null) {
                this.add(item)
            }
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <E> List<E>.save(context: Context, name: String) {
    val folder = context.filesDir.child("lists")
    folder.mkdir()
    val file = folder.child(name + ".json")
    save(file)
}

inline fun <reified E : Any> MutableList<E>.load(context: Context, name: String) {
    val folder = context.filesDir.child("lists")
    folder.mkdir()
    val file = folder.child(name + ".json")
    load(file)
}

@Suppress("NOTHING_TO_INLINE")
inline fun <E : Any> MutableList<E>.load(context: Context, name: String, type: Type) {
    val folder = context.filesDir.child("lists")
    folder.mkdir()
    val file = folder.child(name + ".json")
    load(file, type)
}