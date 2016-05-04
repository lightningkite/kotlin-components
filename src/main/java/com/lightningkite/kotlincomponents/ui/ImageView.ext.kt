package com.lightningkite.kotlincomponents.ui

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import com.lightningkite.kotlincomponents.async.doAsync
import com.lightningkite.kotlincomponents.networking.*
import com.lightningkite.kotlincomponents.viewcontroller.StandardViewController
import org.jetbrains.anko.imageBitmap
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.schedule

/**
 *
 * Created by josep on 3/11/2016.
 *
 */

private val bitmaps: MutableMap<String, Bitmap> = HashMap()


fun ImageView.imageStream(request: NetRequest, minBytes: Long, onResult: (Boolean) -> Unit) {
    doAsync({
        val stream = Networking.stream(request)
        if (stream.isSuccessful) {
            stream.bitmapSized(minBytes)
        } else {
            null
        }
    }, {
        if (it == null) {
            onResult(false)
        } else {
            val code = request.url + UUID.randomUUID().toString()
            if (!isAttachedToWindow) {
                it.recycle()
                return@doAsync
            }
            imageBitmap = it
            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {
                    setImageDrawable(null)
                    it.recycle()
                    removeOnAttachStateChangeListener(this)
                }

                override fun onViewAttachedToWindow(v: View?) {
                }
            })
            onResult(true)
        }
    })
}

@Deprecated("You should use streaming instead.")
fun ImageView.imageLoad(endpoint: NetEndpoint) {
    endpoint.async(NetMethod.GET) { response ->
        if (!response.isSuccessful) return@async
        if (isAttachedToWindowCompat()) {
            var oldBitmap = bitmaps[endpoint.url]
            if (oldBitmap != null) oldBitmap.recycle()
            else addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {
                    bitmaps[endpoint.url]?.recycle()
                    bitmaps.remove(endpoint.url)
                }

                override fun onViewAttachedToWindow(v: View?) {
                }
            })
            val bitmap = response.bitmap()
            if (bitmap != null) {
                bitmaps[endpoint.url] = bitmap
                imageBitmap = bitmap
            }
        }
    }
}

/**
 * This does not work well when used in a list.  for that use
 * imageLoadInList
 */
@Deprecated("You should use streaming instead.")
fun ImageView.imageLoad(url: String, onLoaded: (Boolean) -> Unit = {}) {
    Networking.async(NetMethod.GET, url) { response ->
        if (!response.isSuccessful) {
            onLoaded(false)
            return@async
        }
        onLoaded(true)
        if (isAttachedToWindowCompat()) {
            var oldBitmap = bitmaps[url]
            if (oldBitmap != null) oldBitmap.recycle()
            else addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {
                    bitmaps[url]?.recycle()
                    bitmaps.remove(url)
                }

                override fun onViewAttachedToWindow(v: View?) {
                }
            })
            val bitmap = response.bitmap()
            if (bitmap != null) {
                bitmaps[url] = bitmap
                imageBitmap = bitmap
            }
        }
    }
}

fun ImageView.imageLoadInList(url: String, vc: StandardViewController, onLoadState: (ImageLoadState) -> Unit = {}) {
    var oldBitmap = bitmaps[url]
    var unmakeCalled: AtomicBoolean = AtomicBoolean(false)
    val handler = Handler(Looper.getMainLooper())
    if (oldBitmap != null) {
        this.imageBitmap = oldBitmap
        Timer().schedule(100) {
            handler.post {
                onLoadState(ImageLoadState.EXISTING_LOADED)
            }
        }
        return;
    } else {
        onLoadState(ImageLoadState.LOADING)
        Networking.async(NetMethod.GET, url) { response ->
            if (!response.isSuccessful) return@async
            val it = response.bitmap()
            if (it != null) {
                if (!unmakeCalled.get()) {
                    bitmaps[url] = it
                    imageBitmap = it
                }
            }
            onLoadState(ImageLoadState.NEW_IMAGE_LOADED)
        }
    }

    vc.onUnmake.add {
        unmakeCalled.set(true)
        bitmaps.remove(url)
    }
}

enum class ImageLoadState {
    LOADING,
    NEW_IMAGE_LOADED,
    EXISTING_LOADED
}