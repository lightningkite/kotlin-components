package com.lightningkite.kotlincomponents.viewcontroller

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.util.Stack

public interface ViewControllerStack {
    public fun pushView(newController: ViewController)
    public fun popView()
    public fun optView(tag: String): ViewController?
    public fun getView(tag: String): ViewController {
        return optView(tag) ?: throw IllegalAccessException("No view with tag " + tag + " found.")
    }

    public fun startIntent(intent: Intent, onResult: (result: Int, data: Intent?) -> Unit, options: Bundle = Bundle.EMPTY)
}

/**
 * Created by jivie on 7/16/15.
 */
public fun Stack<ViewController>.saveState(bundle: Bundle, key: String): Bundle {
    val controllerDatas: Array<Parcelable?> = arrayOfNulls(size())
    val classNames: Array<String?> = arrayOfNulls(size())
    var i: Int = 0
    for (controller in this) {
        controllerDatas[i] = controller.saveState()
        classNames[i] = controller.javaClass.getName()
        i++
    }
    bundle.putParcelableArray(key + ".controllerDatas", controllerDatas)
    bundle.putStringArray(key + ".classNames", classNames)
    return bundle
}

public fun Stack<ViewController>.loadState(bundle: Bundle, key: String) {
    val controllerDatas: Array<Parcelable?> = bundle.getParcelableArray(key + ".controllerDatas")!!
    val classNames: Array<String?> = bundle.getStringArray(key + ".classNames")!!
    for (i in 0..classNames.size() - 1) {
        val className = classNames[i]
        val controllerData = controllerDatas[i]
        val controller = Class.forName(className).newInstance() as ViewController
        if (controllerData != null) {
            controller.loadState(controllerData)
        }
        push(controller)
    }
}