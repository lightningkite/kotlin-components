package com.lightningkite.kotlincomponents.networking

import android.content.Context
import com.lightningkite.kotlincomponents.toByteArray
import java.io.IOException

/**
 *
 * Created by jivie on 1/13/16.
 *
 */

open class AndroidFileMockStack(
        val context: Context,
        val responseCodeForUrl: (String, NetMethod, NetBody?) -> Int,
        val urlToAssetPath: (String, NetMethod, NetBody?) -> String
) : NetStack {

    fun readTextFile(successCode: Int, assetPath: String): NetResponse {
        try {
            return NetResponse(successCode, context.assets.open(assetPath).toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
            return NetResponse(404, ByteArray(0))
        }
    }

    override fun sync(method: NetMethod, url: String, body: NetBody, headers: Map<String, String>): NetResponse {
        return readTextFile(responseCodeForUrl(url, method, body), urlToAssetPath(url, method, body))
    }

    companion object {
        fun simple(context: Context, restUrl: String): AndroidFileMockStack = AndroidFileMockStack(
                context,
                { url, method, body -> 200 },
                { url, method, body ->
                    url.replace(restUrl, "") + "." + method.toString() + ".json"
                }
        )
    }
}