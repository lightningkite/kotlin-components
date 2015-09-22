package com.lightningkite.kotlincomponents.animation

import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator

/**
 * Created by jivie on 8/6/15.
 */
public interface AnimationSet {
    public val animateIn: View.(ViewGroup) -> ViewPropertyAnimator
    public val animateOut: View.(ViewGroup) -> ViewPropertyAnimator

    public fun component1(): View.(ViewGroup) -> ViewPropertyAnimator = animateIn
    public fun component2(): View.(ViewGroup) -> ViewPropertyAnimator = animateOut

    public companion object {
        public val fade: AnimationSet = object : AnimationSet {
            override val animateIn: View.(ViewGroup) -> ViewPropertyAnimator = {
                setAlpha(0f)
                animate().alpha(1f).setDuration(300)
            }
            override val animateOut: View.(ViewGroup) -> ViewPropertyAnimator = {
                setAlpha(1f)
                animate().alpha(0f).setDuration(300)
            }
        }
        public val slidePush: AnimationSet = object : AnimationSet {
            override val animateIn: View.(ViewGroup) -> ViewPropertyAnimator = {
                setTranslationX(it.width.toFloat())
                animate().translationX(0f).setDuration(300)
            }
            override val animateOut: View.(ViewGroup) -> ViewPropertyAnimator = {
                setTranslationX(0f)
                animate().translationX(-it.width.toFloat()).setDuration(300)
            }
        }
        public val slidePop: AnimationSet = object : AnimationSet {
            override val animateIn: View.(ViewGroup) -> ViewPropertyAnimator = {
                setTranslationX(-it.width.toFloat())
                animate().translationX(0f).setDuration(300)
            }
            override val animateOut: View.(ViewGroup) -> ViewPropertyAnimator = {
                setTranslationX(0f)
                animate().translationX(it.width.toFloat()).setDuration(300)
            }
        }
    }
}