package com.lightningkite.kotlincomponents.ui

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import kotlin.concurrent.schedule
import com.lightningkite.kotlincomponents.networking.NetEndpoint
import com.lightningkite.kotlincomponents.networking.OkHttpStack
import com.lightningkite.kotlincomponents.viewcontroller.StandardViewController
import org.jetbrains.anko.imageBitmap
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * Created by josep on 3/11/2016.
 *
 */

private val bitmaps: MutableMap<String, Bitmap> = HashMap()

fun ImageView.imageLoad(endpoint: NetEndpoint) {
    OkHttpStack.image(endpoint.url) {
        if (isAttachedToWindow) {
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
            if(it != null) {
                bitmaps[endpoint.url] = it
                imageBitmap = it
            }
        }
    }
}

/**
* This does not work well when used in a list.  for that use
 * imageLoadInList
 */
fun ImageView.imageLoad(url: String) {
    OkHttpStack.image(url) {
        if (isAttachedToWindow) {
            var oldBitmap = bitmaps[url]
            if (oldBitmap != null) oldBitmap.recycle()
            else addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {
                    bitmaps[url]?.recycle()
                    bitmaps.remove(url)
                    println("REMOVE FROM BITMAPS " + bitmaps.size)
                }

                override fun onViewAttachedToWindow(v: View?) {
                }
            })
            if(it != null) {
                bitmaps[url] = it
                imageBitmap = it
            }
        }
    }
}

fun ImageView.imageLoadInList(url: String, vc: StandardViewController, onLoadState: (ImageLoadState) -> Unit = {}) {
    var oldBitmap = bitmaps[url]
    var unmakeCalled :AtomicBoolean = AtomicBoolean(false)
    val handler = Handler(Looper.getMainLooper())
    if(oldBitmap != null) {
        this.imageBitmap = oldBitmap
        Timer().schedule(100) {
            handler.post {
                onLoadState(ImageLoadState.EXISTING_LOADED)
            }
        }
        return;
    } else {
        onLoadState(ImageLoadState.LOADING)
        OkHttpStack.image(url) {
            if(it != null) {
                if(!unmakeCalled.get()) {
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