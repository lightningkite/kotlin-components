package com.lightningkite.kotlincomponents.adapter

import android.content.Context
import android.view.View
import com.lightningkite.kotlincomponents.viewcontroller.ViewControllerStack

/**
 * Created by jivie on 9/2/15.
 */
public class QuickAdaptableViewController<V : View, T>(
        initialItem: T,
        val setup: QuickAdaptableViewController<V, T>.(Context) -> V
) : AdaptableViewControllerImpl<T>(initialItem) {

    protected var stack: ViewControllerStack? = null

    override fun make(context: Context, stack: ViewControllerStack): View {
        this.stack = stack
        return setup(context)
    }
}