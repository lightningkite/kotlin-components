package com.lightningkite.kotlincomponents.collection

import org.jetbrains.anko.collections.forEachReversed
import java.util.*

/**
 * Created by jivie on 4/6/16.
 */
fun <T> stackOf(vararg items: T): Stack<T> {
    val stack = Stack<T>()
    items.forEachReversed {
        stack.push(it as T)
    }
    return stack
}