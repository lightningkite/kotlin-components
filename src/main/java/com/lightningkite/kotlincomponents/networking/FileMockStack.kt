package com.lightningkite.kotlincomponents.networking

import java.io.File
import java.io.IOException
import java.util.*

/**
 *
 * Created by jivie on 1/13/16.
 *
 */

open class FileMockStack(
        val responseCodeForUrl: (String, List<Pair<String, String?>>, NetMethod, NetBody?) -> Int,
        val urlToAssetPath: (String, List<Pair<String, String?>>, NetMethod, NetBody?) -> String
) : NetStack {

    fun fileToByteArray(assetPath: String): ByteArray {
        val scanner = Scanner(File(assetPath))
        val text = scanner.useDelimiter("\\A").next()
        scanner.close()
        return text.toByteArray()
    }

    fun readTextFile(successCode: Int, assetPath: String): NetResponse {
        try {
            return NetResponse(successCode, fileToByteArray(assetPath))
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
        fun simple(restUrl: String): FileMockStack = FileMockStack(
                { url, args, method, body -> 200 },
                { url, args, method, body ->
                    var fullPath = ""
                    fullPath += url.replace(restUrl, "")
                    if (fullPath.endsWith('/')) fullPath = fullPath.substring(0, fullPath.length - 1)
                    fullPath += "."
                    fullPath += args.joinToString(".") { it.first + "." + it.second }
                    fullPath += "."
                    fullPath += method.toString()
                    fullPath += ".json"
                    if (fullPath.startsWith('/')) fullPath = fullPath.substring(1)

                    var shortPath = ""
                    shortPath += url.replace(restUrl, "")
                    if (shortPath.endsWith('/')) shortPath = shortPath.substring(0, shortPath.length - 1)
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