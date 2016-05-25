package com.lightningkite.kotlincomponents.image

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlincomponents.R
import com.lightningkite.kotlincomponents.animation.transitionView
import com.lightningkite.kotlincomponents.asStringOptional
import com.lightningkite.kotlincomponents.networking.NetRequest
import com.lightningkite.kotlincomponents.networking.Networking
import com.lightningkite.kotlincomponents.networking.async
import com.lightningkite.kotlincomponents.observable.KObservable
import com.lightningkite.kotlincomponents.observable.KObservableInterface
import com.lightningkite.kotlincomponents.observable.bind
import com.lightningkite.kotlincomponents.ui.imageStreamExif
import com.lightningkite.kotlincomponents.ui.selector
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import org.jetbrains.anko.*

/**
 * Layouts involving images.
 * Created by jivie on 5/25/16.
 */

fun ViewGroup.layoutImageUpload(
        activity: VCActivity,
        urlObs: KObservableInterface<String?>,
        noImageResource: Int,
        brokenImageResource: Int,
        downloadRequest: NetRequest,
        uploadingObs: KObservable<Boolean>,
        uploadRequest: (Uri) -> NetRequest,
        onUploadError: () -> Unit
): View {
    val loadingObs = KObservable(false)
    return transitionView {
        padding = dip(8)
        imageView {
            bind(urlObs) { url ->
                println(url)
                if (url == null) {
                    //set to default image
                    imageResource = noImageResource
                } else {
                    loadingObs.set(true)
                    imageStreamExif(activity, downloadRequest.copy(url = url), 500) { success ->
                        loadingObs.set(false)
                        if (!success) {
                            //set to default image or broken image
                            imageResource = brokenImageResource
                        }
                    }
                }
            }
        }.lparams(matchParent, matchParent).tag("image")

        progressBar().lparams(wrapContent, wrapContent) { gravity = Gravity.CENTER }.tag("loading")

        bind(loadingObs, uploadingObs) { loading, uploading ->
            if (loading || uploading) animate("loading")
            else animate("image")
        }

        onClick {
            activity.selector(
                    null,
                    R.string.camera to {
                        activity.requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            activity.getImageUriFromCamera() {
                                println(it)
                                if (it != null) uploadImage(context, uploadRequest(it), urlObs, uploadingObs, onUploadError)
                            }
                        }
                    },
                    R.string.gallery to {
                        activity.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
                            activity.getImageUriFromGallery() {
                                println(it)
                                if (it != null) uploadImage(context, uploadRequest(it), urlObs, uploadingObs, onUploadError)
                            }
                        }
                    }
            )
        }


    }
}

inline fun uploadImage(
        context: Context,
        request: NetRequest,
        urlObs: KObservableInterface<String?>,
        uploading: KObservableInterface<Boolean>,
        crossinline onError: () -> Unit
) {
    uploading.set(true)
    try {
        Networking.async(request) {
            uploading.set(false)
            try {
                if (it.isSuccessful) {
                    val newUrl = it.jsonObject().get("url")?.asStringOptional
                    Log.i("image.Layouts", "newUrl=$newUrl")
                    if (newUrl != null) {
                        urlObs.set(newUrl)
                    }
                } else {
                    Log.e("image.Layouts", "failed. ${it.code}: ${it.string()}")
                    onError()
                }
            } catch(e: Exception) {
                uploading.set(false)
                e.printStackTrace()
                onError()
            }
        }
    } catch(e: Exception) {
        uploading.set(false)
        e.printStackTrace()
        onError()
    }
}