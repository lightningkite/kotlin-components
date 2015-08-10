package com.lightningkite.kotlincomponents

/**
 * Created by jivie on 8/7/15.
 */
public fun <T> T.run(runFunc: T.() -> Unit): T {
    runFunc()
    return this
}