package com.lightningkite.kotlincomponents.networking

/**
 * Used to make network calls easier.
 * Created by jivie on 9/2/15.
 */

public object Networking: NetStack {

    var stack: NetStack = OkHttpStack

    override fun sync(method: NetMethod, url: String, body: NetBody, headers: Map<String, String>): NetResponse {
        return stack.sync(method, url, body, headers)
    }

}