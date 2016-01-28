package com.lightningkite.kotlincomponents.async

/**
 * Created by jivie on 1/22/16.
 */

fun debounce(delay: Long, action: () -> Unit): () -> Unit = DebounceZero(delay, action)

fun <A> debounce(delay: Long, action: (A) -> Unit): (A) -> Unit = DebounceOne(delay, action)
fun <A, B> debounce(delay: Long, action: (A, B) -> Unit): (A, B) -> Unit = DebounceTwo(delay, action)
fun <A, B, C> debounce(delay: Long, action: (A, B, C) -> Unit): (A, B, C) -> Unit = DebounceThree(delay, action)

private class DebounceZero(val delay: Long, val action: () -> Unit) : () -> Unit {
    var posted = false
    val runnable = Runnable {
        posted = false
        action()
    }

    override fun invoke() {
        if (posted) {
            Async.handler.removeCallbacks(runnable)
        }
        Async.handler.postDelayed(runnable, delay)
        posted = true
    }
}

private class DebounceOne<A>(val delay: Long, val action: (A) -> Unit) : (A) -> Unit {

    var posted = false

    var a: A? = null

    val runnable = Runnable {
        posted = false
        action(a!!)
    }

    override fun invoke(p1: A) {
        a = p1
        if (posted) {
            Async.handler.removeCallbacks(runnable)
        }
        Async.handler.postDelayed(runnable, delay)
        posted = true
    }
}

private class DebounceTwo<A, B>(val delay: Long, val action: (A, B) -> Unit) : (A, B) -> Unit {

    var posted = false

    var a: A? = null
    var b: B? = null

    val runnable = Runnable {
        posted = false
        action(a!!, b!!)
    }

    override fun invoke(p1: A, p2: B) {
        a = p1
        b = p2
        if (posted) {
            Async.handler.removeCallbacks(runnable)
        }
        Async.handler.postDelayed(runnable, delay)
        posted = true
    }
}

private class DebounceThree<A, B, C>(val delay: Long, val action: (A, B, C) -> Unit) : (A, B, C) -> Unit {

    var posted = false

    var a: A? = null
    var b: B? = null
    var c: C? = null

    val runnable = Runnable {
        posted = false
        action(a!!, b!!, c!!)
    }

    override fun invoke(p1: A, p2: B, p3: C) {
        a = p1
        b = p2
        c = p3
        if (posted) {
            Async.handler.removeCallbacks(runnable)
        }
        Async.handler.postDelayed(runnable, delay)
        posted = true
    }
}