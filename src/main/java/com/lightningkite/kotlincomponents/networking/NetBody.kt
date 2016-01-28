package com.lightningkite.kotlincomponents.networking

import com.google.gson.JsonObject
import com.lightningkite.kotlincomponents.gsonTo
import org.json.JSONObject

/**
 * Created by jivie on 1/28/16.
 */
class NetBody(
        val contentType: ContentType,
        val content: ByteArray
) {
    companion object {
        val EMPTY: NetBody = NetBody(ContentType.NONE, ByteArray(0))
    }
}

fun JSONObject.toNetBody(): NetBody {
    return NetBody(ContentType.JSON, toString().toByteArray())
}

fun JsonObject.toNetBody(): NetBody {
    return NetBody(ContentType.JSON, toString().toByteArray())
}

fun <T : Any> T.gsonToNetBody(): NetBody {
    return NetBody(ContentType.JSON, gsonTo().toByteArray())
}

fun String.toJsonNetBody(): NetBody {
    return NetBody(ContentType.JSON, toByteArray())
}