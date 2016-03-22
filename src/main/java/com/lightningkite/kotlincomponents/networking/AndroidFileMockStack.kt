package com.lightningkite.kotlincomponents.networking

import android.content.Context
import com.lightningkite.kotlincomponents.toByteArray
import java.io.File
import java.io.IOException

/**
 *
 * Created by jivie on 1/13/16.
 *
 */

open class AndroidFileMockStack(
        val context: Context,
        val responseCodeForUrl: (String, List<Pair<String, String?>>, NetMethod, NetBody?) -> Int,
        val urlToAssetPath: (String, List<Pair<String, String?>>, NetMethod, NetBody?) -> String
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
        val i = url.indexOf('?')
        var justUrl = ""
        var args = listOf<Pair<String, String?>>()
        if (i == -1) {
            justUrl = url
        } else {
            justUrl = url.substring(0, i)
            args = url.substring(i + 1).split('&').map {
                var pair = it.split('=')
                if (pair.size == 2) {
                    pair[0] to pair[1]
                } else if (pair.size == 1) {
                    pair[0] to null
                } else throw IllegalArgumentException()
            }
        }
        return readTextFile(responseCodeForUrl(justUrl, args, method, body), urlToAssetPath(justUrl, args, method, body))
    }

    companion object {
        fun simple(context: Context, restUrl: String): AndroidFileMockStack = AndroidFileMockStack(
                context,
                { url, args, method, body -> 200 },
                { url, args, method, body ->
                    var fullPath = ""
                    fullPath += url.replace(restUrl, "")
                    fullPath += "."
                    fullPath += args.joinToString(".") { it.first + "." + it.second }
                    fullPath += "."
                    fullPath += method.toString()
                    fullPath += ".json"
                    if (fullPath.startsWith('/')) fullPath = fullPath.substring(1)

                    var shortPath = ""
                    shortPath += url.replace(restUrl, "")
                    shortPath += "."
                    shortPath += method.toString()
                    shortPath += ".json"
                    if (shortPath.startsWith('/')) shortPath = shortPath.substring(1)

                    if (File(fullPath).exists())
                        fullPath
                    else
                        shortPath
                }
        )
    }
}