package com.lightningkite.kotlincomponents.networking

/**
 * Created by jivie on 2/18/16.
 */
inline fun String.urlAddQueryParameter(key: String): String {
    if (this.contains('?')) {
        return this + "&" + key
    } else {
        return this + "?" + key
    }
}

inline fun String.urlAddQueryParameter(key: String, value: String): String {
    if (this.contains('?')) {
        return this + "&" + key + "=" + value
    } else {
        return this + "?" + key + "=" + value
    }
}

inline fun String.urlAddOptionalQueryParameter(key: String, value: String?): String {
    if (value == null) return this
    if (this.contains('?')) {
        return this + "&" + key + "=" + value
    } else {
        return this + "?" + key + "=" + value
    }
}

inline fun String.urlSub(value: String) = this + "/$value"
inline fun String.urlSub(value: Long) = this + "/$value"
inline fun String.urlSub(value: Int) = this + "/$value"

inline fun <reified R : Any> NetStack.gsonGet(
        url: String,
        headers: Map<String, String> = NetHeader.EMPTY,
        noinline onError: (NetResponse) -> Unit,
        noinline onResult: (R) -> Unit
): Unit {
    get(url, headers) {
        if (it.isSuccessful) {
            val result = it.result<R>()
            if (result == null) onError(it)
            else onResult(result)
        } else {
            onError(it)
        }
    }
}

inline fun <reified R : Any> NetStack.gsonPost(
        url: String,
        body: Any,
        headers: Map<String, String> = NetHeader.EMPTY,
        noinline onError: (NetResponse) -> Unit,
        noinline onResult: (R) -> Unit
): Unit {
    post(url, body.gsonToNetBody(), headers) {
        if (it.isSuccessful) {
            val result = it.result<R>()
            if (result == null) onError(it)
            else onResult(result)
        } else {
            onError(it)
        }
    }
}

inline fun <reified R : Any> NetStack.gsonPut(
        url: String,
        body: Any,
        headers: Map<String, String> = NetHeader.EMPTY,
        noinline onError: (NetResponse) -> Unit,
        noinline onResult: (R) -> Unit
): Unit {
    put(url, body.gsonToNetBody(), headers) {
        if (it.isSuccessful) {
            val result = it.result<R>()
            if (result == null) onError(it)
            else onResult(result)
        } else {
            onError(it)
        }
    }
}

inline fun <reified R : Any> NetStack.gsonPatch(
        url: String,
        body: Any,
        headers: Map<String, String> = NetHeader.EMPTY,
        noinline onError: (NetResponse) -> Unit,
        noinline onResult: (R) -> Unit
): Unit {
    patch(url, body.gsonToNetBody(), headers) {
        if (it.isSuccessful) {
            val result = it.result<R>()
            if (result == null) onError(it)
            else onResult(result)
        } else {
            onError(it)
        }
    }
}

inline fun <reified R : Any> NetStack.gsonDelete(
        url: String,
        body: Any? = null,
        headers: Map<String, String> = NetHeader.EMPTY,
        noinline onError: (NetResponse) -> Unit,
        noinline onResult: (R) -> Unit
): Unit {
    delete(url, body?.gsonToNetBody() ?: NetBody.EMPTY, headers) {
        if (it.isSuccessful) {
            val result = it.result<R>()
            if (result == null) onError(it)
            else onResult(result)
        } else {
            onError(it)
        }
    }
}

inline fun NetStack.gsonGet(
        url: String,
        headers: Map<String, String> = NetHeader.EMPTY,
        noinline onError: (NetResponse) -> Unit,
        noinline onResult: () -> Unit
): Unit {
    get(url, headers) {
        if (it.isSuccessful) {
            onResult()
        } else {
            onError(it)
        }
    }
}

inline fun NetStack.gsonPost(
        url: String,
        body: Any,
        headers: Map<String, String> = NetHeader.EMPTY,
        noinline onError: (NetResponse) -> Unit,
        noinline onResult: () -> Unit
): Unit {
    post(url, body.gsonToNetBody(), headers) {
        if (it.isSuccessful) {
            onResult()
        } else {
            onError(it)
        }
    }
}

inline fun NetStack.gsonPut(
        url: String,
        body: Any,
        headers: Map<String, String> = NetHeader.EMPTY,
        noinline onError: (NetResponse) -> Unit,
        noinline onResult: () -> Unit
): Unit {
    put(url, body.gsonToNetBody(), headers) {
        if (it.isSuccessful) {
            onResult()
        } else {
            onError(it)
        }
    }
}

inline fun NetStack.gsonPatch(
        url: String,
        body: Any,
        headers: Map<String, String> = NetHeader.EMPTY,
        noinline onError: (NetResponse) -> Unit,
        noinline onResult: () -> Unit
): Unit {
    patch(url, body.gsonToNetBody(), headers) {
        if (it.isSuccessful) {
            onResult()
        } else {
            onError(it)
        }
    }
}

inline fun NetStack.gsonDelete(
        url: String,
        body: Any? = null,
        headers: Map<String, String> = NetHeader.EMPTY,
        noinline onError: (NetResponse) -> Unit,
        noinline onResult: () -> Unit
): Unit {
    delete(url, body?.gsonToNetBody() ?: NetBody.EMPTY, headers) {
        if (it.isSuccessful) {
            onResult()
        } else {
            onError(it)
        }
    }
}