package com.lightningkite.kotlincomponents.layview

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.lightningkite.kotlincomponents.parcel.Bundler

/**
 * Created by jivie on 6/26/15.
 */
public abstract class BaseViewController : ViewController {
    public var view: View? = null
    public var context: Context? = null

    override fun make(context: Context): View {
        this.context = context
        val newView = make()
        view = newView
        return newView
    }

    public abstract fun make(): View

    override fun loadState(state: Parcelable) {
        Bundler.fromBundle(state as Bundle, this, this.javaClass)
    }

    override fun saveState(): Parcelable? {
        return Bundler.toBundle(this, this.javaClass)
    }

    override fun dispose(view: View) {
        this.context = null
        this.view = null
    }

}