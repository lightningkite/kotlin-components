package com.lightningkite.kotlincomponents.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import com.lightningkite.kotlincomponents.async.doAsync
import com.lightningkite.kotlincomponents.image.getBitmapFromUri
import com.lightningkite.kotlincomponents.networking.*
import com.lightningkite.kotlincomponents.viewcontroller.StandardViewController
import org.jetbrains.anko.imageBitmap
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.schedule

/**
 *
 * Created by josep on 3/11/2016.
 *
 */


val ImageView_previousBitmap: MutableMap<ImageView, Bitmap> = HashMap()
val ImageView_previousListener: MutableMap<ImageView, View.OnAttachStateChangeListener> = HashMap()

inline fun ImageView.imageStream(url: String, minBytes: Long? = null, crossinline onResult: (Boolean) -> Unit)
        = imageStream(NetRequest(NetMethod.GET, url), minBytes, onResult)

inline fun ImageView.imageStream(request: NetRequest, minBytes: Long? = null, crossinline onResult: (Boolean) -> Unit) {
    doAsync({
        val stream = Networking.stream(request)
        if (stream.isSuccessful) {
            if (minBytes != null) {
                stream.bitmapSized(minBytes)
            } else {
                stream.bitmap()
            }
        } else {
            null
        }
    }, {
        if (it == null) {
            onResult(false)
        } else {
            if (!isAttachedToWindowCompat()) {
                it.recycle()
                return@doAsync
            }

            imageBitmap = it
            val newListener = object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {
                    setImageDrawable(null)
                    it.recycle()
                    ImageView_previousBitmap.remove(this@imageStream)
                    removeOnAttachStateChangeListener(this)
                }

                override fun onViewAttachedToWindow(v: View?) {
                }
            }

            ImageView_previousBitmap[this]?.recycle()
            ImageView_previousBitmap[this] = it
            if (ImageView_previousListener[this] != null) {
                removeOnAttachStateChangeListener(ImageView_previousListener[this])
            }
            ImageView_previousListener[this] = newListener
            addOnAttachStateChangeListener(ImageView_previousListener[this])

            onResult(true)
        }
    })
}

fun ImageView.imageStreamExif(context: Context, request: NetRequest, maxDimension: Int = Int.MAX_VALUE, onResult: (Boolean) -> Unit) {
    val tempFile = File.createTempFile("image", "jpg", context.cacheDir)
    doAsync({
        val stream = Networking.stream(request)
        if (stream.isSuccessful) {
            stream.download(tempFile)
            context.getBitmapFromUri(Uri.fromFile(tempFile), maxDimension)
        } else {
            null
        }
    }, {
        if (it == null) {
            onResult(false)
        } else {
            if (!isAttachedToWindowCompat()) {
                it.recycle()
                try {
                    tempFile.delete()
                } catch(e: Exception) {/*Squish*/
                }
                return@doAsync
            }

            imageBitmap = it
            val newListener = object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {
                    setImageDrawable(null)
                    it.recycle()
                    ImageView_previousBitmap.remove(this@imageStreamExif)
                    removeOnAttachStateChangeListener(this)
                    try {
                        tempFile.delete()
                    } catch(e: Exception) {/*Squish*/
                    }
                }

                override fun onViewAttachedToWindow(v: View?) {
                }
            }

            ImageView_previousBitmap[this]?.recycle()
            ImageView_previousBitmap[this] = it
            if (ImageView_previousListener[this] != null) {
                removeOnAttachStateChangeListener(ImageView_previousListener[this])
                try {
                    tempFile.delete()
                } catch(e: Exception) {/*Squish*/
                }
            }
            ImageView_previousListener[this] = newListener
            addOnAttachStateChangeListener(ImageView_previousListener[this])

            onResult(true)
        }
    })
}

private val bitmaps: MutableMap<String, Bitmap> = HashMap()
@Deprecated("You should use streaming instead.")
fun ImageView.imageLoad(endpoint: NetEndpoint) {
    endpoint.async(NetMethod.GET) { response ->
        if (!response.isSuccessful) return@async
        if (isAttachedToWindowCompat()) {
            val oldBitmap = bitmaps[endpoint.url]
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
            val oldBitmap = bitmaps[url]
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
    val oldBitmap = bitmaps[url]
    val unmakeCalled: AtomicBoolean = AtomicBoolean(false)
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