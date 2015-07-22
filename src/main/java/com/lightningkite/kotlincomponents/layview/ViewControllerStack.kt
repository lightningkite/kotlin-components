package com.lightningkite.kotlincomponents.layview

import android.os.Bundle
import android.os.Parcelable
import java.util.Stack

/**
 * Created by jivie on 7/16/15.
 */
public class ViewControllerStack() : Stack<ViewController>(){

    public val top:ViewController
        get() = peek()

    public fun saveState(bundle: Bundle, key:String): Bundle {
        val controllerDatas: Array<Parcelable?> = arrayOfNulls(size())
        val classNames: Array<String?> = arrayOfNulls(size())
        var i: Int = 0
        for (controller in this) {
            controllerDatas[i] = controller.saveState()
            classNames[i] = controller.javaClass.getName()
            i++
        }
        bundle.putParcelableArray(key+".controllerDatas", controllerDatas)
        bundle.putStringArray(key+".classNames", classNames)
        return bundle
    }

    public fun loadState(bundle: Bundle, key:String) {
        val controllerDatas: Array<Parcelable?> = bundle.getParcelableArray(key+".controllerDatas")!!
        val classNames: Array<String?> = bundle.getStringArray(key+".classNames")!!
        for (i in 0 .. classNames.size()-1) {
            val className = classNames[i]
            val controllerData = controllerDatas[i]
            val controller = Class.forName(className).newInstance() as ViewController
            if (controllerData != null) {
                controller.loadState(controllerData)
            }
            push(controller)
        }
    }
}