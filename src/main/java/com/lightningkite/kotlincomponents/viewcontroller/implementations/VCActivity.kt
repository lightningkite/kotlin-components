package com.lightningkite.kotlincomponents.viewcontroller.implementations

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.runAll
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

    val onActivityResult = ArrayList<(Int, Int, Intent?) -> Unit>()

    fun startIntent(intent: Intent, options: Bundle = Bundle.EMPTY, onResult: (Int, Intent?) -> Unit = { a, b -> }) {
        val generated: Int = (Math.random() * Int.MAX_VALUE).toInt()
        returns[generated] = onResult
        startActivityForResult(intent, generated, options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onActivityResult.runAll(requestCode, resultCode, data)
        returns[requestCode]?.invoke(resultCode, data)
        returns.remove(requestCode)
    }

    open val defaultAnimation: AnimationSet? = AnimationSet.fade

    fun attach(newContainer: VCContainer) {
        vcView.attach(newContainer)
    }

    lateinit var vcView: VCView

    var savedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        vcView = VCView(this)
        setContentView(vcView)
    }

    fun onCreate(savedInstanceState: Bundle?, shouldSetContent: Boolean) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        vcView = VCView(this)
        if (shouldSetContent) {
            setContentView(vcView)
        }
    }

    val onResume = HashSet<()->Unit>()
    override fun onResume() {
        super.onResume()
        onResume.runAll()
    }

    val onPause = HashSet<()->Unit>()
    override fun onPause() {
        onPause.runAll()
        super.onPause()
    }

    val onSaveInstanceState = HashSet<(outState: Bundle)->Unit>()
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        onSaveInstanceState.runAll(outState)
    }

    val onLowMemory = HashSet<()->Unit>()
    override fun onLowMemory() {
        super.onLowMemory()
        onLowMemory.runAll()
    }

    override fun onBackPressed() {
        vcView.container?.onBackPressed {
            super.onBackPressed()
        } ?: super.onBackPressed()
    }

    val onDestroy = HashSet<()->Unit>()
    override fun onDestroy() {
        vcView.detatch()
        vcView.unmake()
        onDestroy.runAll()
        super.onDestroy()
    }

    val requestReturns: HashMap<Int, (Map<String, Int>) -> Unit> = HashMap()

    /**
     * Requests a bunch of permissions and returns a map of permissions that were previously ungranted and their new status.
     */
    fun requestPermissions(permission: Array<String>, onResult: (Map<String, Int>) -> Unit) {
        val ungranted = permission.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (ungranted.isNotEmpty()) {
            val generated: Int = (Math.random() * Int.MAX_VALUE).toInt()

            requestReturns[generated] = onResult

            ActivityCompat.requestPermissions(this, ungranted.toTypedArray(), generated)

        }
    }

    /**
     * Requests a single permissiona and returns whether it was granted or not.
     */
    fun requestPermission(permission: String, onResult: (Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            val generated: Int = (Math.random() * Int.MAX_VALUE).toInt()
            requestReturns[generated] = {
                onResult(it[permission] == PackageManager.PERMISSION_GRANTED)
            }
            ActivityCompat.requestPermissions(this, arrayOf(permission), generated)

        } else {
            onResult(true)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val map = HashMap<String, Int>()
        for (i in permissions.indices) {
            map[permissions[i]] = grantResults[i]
        }
        requestReturns[requestCode]?.invoke(map)

        requestReturns.remove(requestCode)
    }
}