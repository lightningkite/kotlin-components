package com.lightningkite.kotlincomponents.layview

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by jivie on 7/22/15.
 */

public abstract class ViewControllerFragment() : Fragment() {

    private var viewController: ViewController? = null

    abstract protected fun make(arguments: Bundle?): ViewController;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return viewController?.make(getActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewController = make(getArguments())
    }

    override fun onDestroyView() {
        viewController?.dispose(getView())
        super.onDestroyView()
    }
}