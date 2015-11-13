package com.lightningkite.kotlincomponents.viewcontroller.implementations

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContainer
import java.util.*

/**
 * All activities hosting [ViewController]s must be extended from this one.
 * It handles the calling of other activities with [onActivityResult], the attaching of a
 * [VCContainer], and use the back button on the [VCContainer].
 * Created by jivie on 10/12/15.
 */
abstract class VCActivity : Activity() {

    companion object {
        val returns: HashMap<Int, (Int, Intent?) -> Unit> = HashMap()
    }

    fun startIntent(intent: Intent, options: Bundle = Bundle.EMPTY, onResult: (Int, Intent?) -> Unit = { a, b -> }) {
        val generated: Int = (Math.random() * Int.MAX_VALUE).toInt()
        returns[generated] = onResult
        startActivityForResult(intent, generated, options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        returns[requestCode]?.invoke(resultCode, data)
        returns.remove(requestCode)
    }

    open val defaultAnimation: AnimationSet? = AnimationSet.fade

    fun attach(newContainer: VCContainer) {
        vcView.attach(newContainer)
    }

    lateinit var vcView: VCView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vcView = VCView(this)
        setContentView(vcView)
    }

    override fun onBackPressed() {
        vcView.container?.onBackPressed{
            super.onBackPressed()
        } ?: super.onBackPressed()
    }

    override fun onDestroy() {
        vcView.detatch()
        vcView.unmake()
        super.onDestroy()
    }

}