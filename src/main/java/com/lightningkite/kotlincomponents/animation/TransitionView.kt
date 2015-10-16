package com.lightningkite.kotlincomponents.animation

import android.content.Context
import android.view.View
import android.view.ViewManager
import org.jetbrains.anko._FrameLayout
import org.jetbrains.anko.custom.ankoView
import java.util.*

/**
 * A container view that transitions between the child views.
 * Use this class Anko-style, and append the views with the [tag] function.
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

    /**
     * Tags a view with [myTag].
     * @param myTag The tag used to access this view later.
     */
    public fun <T : View> T.tag(myTag: String): T {
        views.put(myTag, this)
        visibility = View.INVISIBLE
        return this
    }

    /**
     * Animates using [set] to the view designated by [tag].
     * @param tag The view to animate to.
     * @param set The animation set for animating.
     */
    public fun animate(tag: String, set: AnimationSet = AnimationSet.fade) {
        //val (animateIn, animateOut) = set
        val animateIn = set.animateIn
        val animateOut = set.animateOut
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
        currentView = views.get(tag)!!
        currentView.visibility = View.VISIBLE
    }
}

@Suppress("NOTHING_TO_INLINE") public inline fun ViewManager.transitionView() = transitionView {}
public inline fun ViewManager.transitionView(init: TransitionView.() -> Unit): TransitionView {
    return ankoView({ TransitionView(it) }, init)
}