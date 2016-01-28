package com.lightningkite.kotlincomponents.networking

import com.squareup.okhttp.Headers
import com.squareup.okhttp.RequestBody

/**
 * Created by shanethompson on 1/27/16.
 */
class MockNetStack(val createMockResponse:(url:String, body:String?) -> NetResponse) : NetStack {

    override fun syncGet(headers: Headers, url: String): NetResponse {
        return createMockResponse(url, null)
    }

    override fun syncPost(headers: Headers, url: String, body: String): NetResponse {
        return createMockResponse(url, body)
    }

    override fun syncPut(headers: Headers, url: String, body: String): NetResponse {
        return createMockResponse(url, body)
    }

    override fun syncPatch(headers: Headers, url: String, body: String): NetResponse {
        return createMockResponse(url, body)
    }

    override fun syncDelete(headers: Headers, url: String, body: String): NetResponse {
        return createMockResponse(url, body)
    }
}