package com.lightningkite.kotlincomponents.async

import android.os.Handler
import android.os.Looper
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Various functions to do things asynchronously.
 * Created by jivie on 9/2/15.
 */

public object Async {
    val runnableQueue = LinkedBlockingQueue<Runnable>();
    private val NUMBER_OF_CORES: Int = Runtime.getRuntime().availableProcessors();
    private val KEEP_ALIVE_TIME: Long = 1;
    private val KEEP_ALIVE_TIME_UNIT: TimeUnit = TimeUnit.SECONDS;
    val threadPool = ThreadPoolExecutor(
            NUMBER_OF_CORES, // Initial pool size
            NUMBER_OF_CORES, // Max pool size
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            runnableQueue);

    val handler: Handler = Handler(Looper.getMainLooper())
}

/**
 * Runs [action] asynchronously.
 * @param action The lambda to run asynchronously.
 */
public fun <T> doAsync(action: () -> T) {
    Async.threadPool.execute({
        try {
            val result = action()
        } catch(e: Exception) {
            Async.handler.post {
                throw e
            }
        }
    })
}

/**
 * Runs [action] asynchronously with its result being dealt with on the UI thread in [uiThread].
 * @param action The lambda to run asynchronously.
 * @param uiThread The lambda to run with the result of [action] on the UI thread.
 */
public fun <T> doAsync(action: () -> T, uiThread: (T) -> Unit) {
    Async.threadPool.execute({
        try {
            val result = action()
            Async.handler.post {
                uiThread(result)
            }
        } catch(e: Exception) {
            Async.handler.post {
                throw e
            }
        }
    })
}