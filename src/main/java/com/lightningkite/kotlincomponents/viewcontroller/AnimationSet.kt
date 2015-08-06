package com.lightningkite.kotlincomponents.viewcontroller

import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import org.jetbrains.anko.width

/**
 * Created by jivie on 8/6/15.
 */
public interface AnimationSet {
    public val animateIn: View.(ViewGroup) -> ViewPropertyAnimator
    public val animateOut: View.(ViewGroup) -> ViewPropertyAnimator

    public companion object {
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