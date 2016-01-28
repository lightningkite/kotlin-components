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

open class FileMockStack(val context: Context, val responseCodeForUrl:(String) -> Int, val urlToAssetPath:(String, NetBody?)->String) : NetStack {

    fun readTextFile(successCode:Int, assetPath:String): NetResponse {
        try {
            return NetResponse(successCode, context.assets.open(assetPath).toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
            return NetResponse(404, ByteArray(0))
        }
    }

    override fun sync(method: NetMethod, url: String, body: NetBody, headers: Map<String, String>): NetResponse {
        return readTextFile(responseCodeForUrl(url), urlToAssetPath(url, body))
    }
}