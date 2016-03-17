package com.lightningkite.kotlincomponents.animation

import android.content.Context
import android.view.Gravity
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
class TransitionView(context: Context) : _FrameLayout(context) {
    private val views: HashMap<String, View> = HashMap()
    private var currentView: View = View(context)
    fun getCurrentView() :View { return currentView}
    var defaultAnimation: AnimationSet = AnimationSet.fade

    fun addView(tag: String, child: View) {
        super.addView(child)
        views.put(tag, child)
        child.visibility = View.INVISIBLE
    }

    fun removeView(tag: String) {
        super.removeView(views.remove(tag))
    }

    override fun generateDefaultLayoutParams(): LayoutParams? {
        val params = super.generateDefaultLayoutParams()
        params.gravity = Gravity.CENTER
        return params
    }

    /**
     * Tags a view with [myTag].
     * @param myTag The tag used to access this view later.
     */
    fun <T : View> T.tag(myTag: String): T {
        views.put(myTag, this)
        visibility = View.INVISIBLE
        return this
    }

    /**
     * Animates using [set] to the view designated by [tag].
     * @param tag The view to animate to.
     * @param set The animation set for animating.
     */
    fun animate(tag: String, set: AnimationSet = defaultAnimation) {
        if (views[tag] == currentView) return;
        //val (animateIn, animateOut) = set
        val animateIn = set.animateIn
        val animateOut = set.animateOut
        val oldView = currentView
        val newView = views[tag] ?: return

        newView.visibility = View.VISIBLE
        newView.animateIn(this).start()
        oldView.animateOut(this).withEndAction {
            oldView.visibility = View.INVISIBLE
        }.start()

        currentView = newView
    }

    fun jump(tag: String) {
        currentView.visibility = View.INVISIBLE
        currentView = views[tag]!!
        currentView.visibility = View.VISIBLE
    }
}

@Suppress("NOTHING_TO_INLINE") inline fun ViewManager.transitionView() = transitionView {}
inline fun ViewManager.transitionView(init: TransitionView.() -> Unit): TransitionView {
    return ankoView({ TransitionView(it) }, init)
}