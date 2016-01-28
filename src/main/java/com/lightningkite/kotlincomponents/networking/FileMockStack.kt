package com.lightningkite.kotlincomponents.networking

import android.content.Context
import com.lightningkite.kotlincomponents.toByteArray
import com.squareup.okhttp.Headers
import java.io.*

/**
 *
 * Created by jivie on 1/13/16.
 *
 */

open class FileMockStack(val context: Context, val responseCodeForUrl:(String) -> Int, val urlToAssetPath:(String, String?)->String) : NetStack {

    fun readTextFile(successCode:Int, assetPath:String): NetResponse {
        try {
            return NetResponse(successCode, context.assets.open(assetPath).toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
            return NetResponse(404, ByteArray(0))
        }
    }

    override fun syncGet(headers: Headers, url: String): NetResponse {
        return readTextFile(responseCodeForUrl(url), urlToAssetPath(url, null) + ".get.txt")
    }

    override fun syncPost(headers: Headers, url: String, body: String): NetResponse {
        return readTextFile(responseCodeForUrl(url), urlToAssetPath(url, body) + ".post.txt")
    }

    override fun syncPut(headers: Headers, url: String, body: String): NetResponse {
        return readTextFile(responseCodeForUrl(url), urlToAssetPath(url, body) + ".put.txt")
    }

    override fun syncPatch(headers: Headers, url: String, body: String): NetResponse {
        return readTextFile(responseCodeForUrl(url), urlToAssetPath(url, body) + ".patch.txt")
    }

    override fun syncDelete(headers: Headers, url: String, body: String): NetResponse {
        return readTextFile(responseCodeForUrl(url), urlToAssetPath(url, body) + ".delete.txt")
    }
}