package com.lightningkite.kotlincomponents.animation

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlincomponents.context
import org.jetbrains.anko._FrameLayout
import org.jetbrains.anko.visibility
import java.util.HashMap

/**
 * Created by jivie on 8/26/15.
 */
public class TransitionView(context: Context) : _FrameLayout(context) {
    private val views: HashMap<String, View> = HashMap()
    private var currentView: View = View(context)

    public fun addView(tag: String, child: View) {
        super.addView(child)
        views.put(tag, child)
        child.visibility = View.INVISIBLE
    }

    public fun removeView(tag: String) {
        super.removeView(views.remove(tag))
    }

    public fun <T : View> T.tag(myTag: String): T {
        views.put(myTag, this)
        visibility = View.INVISIBLE
        return this
    }

    public fun animate(tag: String, set: AnimationSet = AnimationSet.fade) {
        val (animateIn, animateOut) = set
        val oldView = currentView
        val newView = views.get(tag) ?: return

        newView.visibility = View.VISIBLE
        newView.animateIn(this).start()
        oldView.animateOut(this).withEndAction {
            oldView.visibility = View.INVISIBLE
        }.start()

        currentView = newView
    }

    public fun jump(tag: String) {
        currentView.visibility = View.INVISIBLE
        currentView = views.get(tag)
        currentView.visibility = View.VISIBLE
    }
}

public fun ViewGroup.transitionView(initFunc: TransitionView.() -> Unit): TransitionView {
    val view = TransitionView(context)
    view.initFunc()
    addView(view)
    return view
}