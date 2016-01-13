package com.lightningkite.kotlincomponents.networking

import android.content.Context
import com.lightningkite.kotlincomponents.toByteArray
import com.squareup.okhttp.Headers
import com.squareup.okhttp.RequestBody
import java.io.*

/**
 * Created by jivie on 1/13/16.
 */
open class FileMockNetworking(val context: Context, val urlToAssetPath:(String)->String) : NetStack {

    fun readTextFile(successCode:Int, assetPath:String): NetResponse {
        try {
            return NetResponse(successCode, context.assets.open(assetPath).toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
            return NetResponse(404, ByteArray(0))
        }
    }

    override fun syncGet(headers: Headers, url: String): NetResponse {
        return readTextFile(200, urlToAssetPath(url) + ".get.txt")
    }

    override fun syncPost(headers: Headers, url: String, body: RequestBody): NetResponse {
        return readTextFile(201, urlToAssetPath(url) + ".post.txt")
    }

    override fun syncPut(headers: Headers, url: String, body: RequestBody): NetResponse {
        return readTextFile(200, urlToAssetPath(url) + ".put.txt")
    }

    override fun syncPatch(headers: Headers, url: String, body: RequestBody): NetResponse {
        return readTextFile(200, urlToAssetPath(url) + ".patch.txt")
    }

    override fun syncDelete(headers: Headers, url: String, body: RequestBody): NetResponse {
        return readTextFile(200, urlToAssetPath(url) + ".delete.txt")
    }
}