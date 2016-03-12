package com.lightningkite.kotlincomponents.ui

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.lightningkite.kotlincomponents.networking.NetEndpoint
import org.jetbrains.anko.imageBitmap
import java.util.*

/**
 * Created by josep on 3/11/2016.
 */
private val bitmaps: MutableMap<ImageView, Bitmap> = HashMap()

fun ImageView.imageLoad(endpoint: NetEndpoint) {
    endpoint.get<Bitmap>(onError = { true }) {
        if (isAttachedToWindow) {
            var oldBitmap = bitmaps[this]
            if (oldBitmap != null) oldBitmap.recycle()
            else addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {
                    bitmaps[this@imageLoad]?.recycle()
                    bitmaps.remove(this@imageLoad)
                }

                override fun onViewAttachedToWindow(v: View?) {
                }
            })
            bitmaps[this] = it
            imageBitmap = it
        }
    }
}

fun ImageView.imageLoad(url: String) {
    NetEndpoint.fromUrl(url).get<Bitmap>(onError = { true }) {
        if (isAttachedToWindow) {
            var oldBitmap = bitmaps[this]
            if (oldBitmap != null) oldBitmap.recycle()
            else addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {
                    bitmaps[this@imageLoad]?.recycle()
                    bitmaps.remove(this@imageLoad)
                }

                override fun onViewAttachedToWindow(v: View?) {
                }
            })
            bitmaps[this] = it
            imageBitmap = it
        }
    }
}