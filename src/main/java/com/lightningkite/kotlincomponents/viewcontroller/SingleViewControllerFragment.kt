package com.lightningkite.kotlincomponents.viewcontroller

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by jivie on 7/22/15.
 */

public abstract class SingleViewControllerFragment() : Fragment(), ViewControllerStack {

    private var viewController: ViewController? = null

    abstract protected fun make(arguments: Bundle?): ViewController;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return viewController?.make(getActivity(), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Fragment>.onCreate(savedInstanceState)
        viewController = make(getArguments())
    }

    override fun onDestroyView() {
        viewController?.dispose(getView())
        super<Fragment>.onDestroyView()
    }

    override fun pushView(newController: ViewController, onResult: (result: Any?) -> Unit) {
        //ignore
    }

    override fun popView(): Boolean = false

    override fun resetView(newController: ViewController) {
        //ignore
    }

    override fun replaceView(newController: ViewController) {
        //ignore
    }

    private var onResultLambda: (result: Int, data: Intent?) -> Unit = { result, data -> }
    override fun startIntent(intent: Intent, onResult: (result: Int, data: Intent?) -> Unit, options: Bundle) {
        onResultLambda = onResult
        startActivityForResult(intent, 0, options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onResultLambda(resultCode, data)
    }
}