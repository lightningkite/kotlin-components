package com.lightningkite.kotlincomponents.async

import android.os.Handler
import android.os.Looper
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
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

public fun <T> doAsync(action: () -> T) {
    Async.threadPool.execute(object : Runnable {
        override fun run() {
            val result = action()
        }
    })
}

public fun <T> doAsync(action: () -> T, uiThread: (T) -> Unit) {
    Async.threadPool.execute(object : Runnable {
        override fun run() {
            val result = action()
            Async.handler.post {
                uiThread(result)
            }
        }
    })
}