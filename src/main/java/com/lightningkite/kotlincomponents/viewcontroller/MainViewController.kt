package com.lightningkite.kotlincomponents.viewcontroller

import android.support.v7.widget.ActionMenuView
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlincomponents.observable.KObservable
import com.lightningkite.kotlincomponents.observable.bind
import com.lightningkite.kotlincomponents.ui.elevationCompat
import com.lightningkite.kotlincomponents.verticalLayout
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContainer
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCStack
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import org.jetbrains.anko.appcompat.v7.actionMenuView
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * Created by jivie on 3/15/16.
 */
abstract class MainViewController(val backResource: Int, val styleToolbar: Toolbar.() -> Unit) : StandardViewController() {

    var menuResource: Int = 0
    var toolbar: Toolbar? = null
    var actionMenu: ActionMenuView? = null

    val alwaysShowBackObs: KObservable<Boolean> = KObservable(false)
    var alwaysShowBack by alwaysShowBackObs

    fun setToolbarTitle(input: String) {
        toolbar?.apply {
            val index = input.indexOf('\n')
            if (index == -1) {
                this.title = input
            } else {
                this.title = input.substring(0, index)
                this.subtitle = input.substring(index + 1)
            }
        }

    }

    override fun makeView(activity: VCActivity): View = verticalLayout(activity) {

        toolbar = toolbar {
            elevationCompat = dip(4).toFloat()
            styleToolbar()
            actionMenu = actionMenuView {

            }.lparams(Gravity.RIGHT)
        }.lparams(matchParent, wrapContent)

        coordinatorLayout {
            makeSubview(activity).lparams(matchParent, matchParent)
        }.lparams(matchParent, 0, 1f)
    }

    abstract fun ViewGroup.makeSubview(activity: VCActivity): View

    fun attachMenu(activity: VCActivity, stack: VCStack, onMenuClickObs: KObservable<() -> Unit>) {
        bind(alwaysShowBackObs) {
            toolbar?.apply {
                if (stack.size > 1 || alwaysShowBack) {
                    setNavigationIcon(backResource)
                    setNavigationOnClickListener { activity.onBackPressed() }
                } else {
                    setNavigationIcon(menuResource)
                    setNavigationOnClickListener { onMenuClickObs.get()() }
                }
                setToolbarTitle(stack.getTitle(resources))
            }
        }
        bind(stack.onSwap, stack.current) {
            toolbar?.apply {
                if (stack.size > 1 || alwaysShowBack) {
                    setNavigationIcon(backResource)
                    setNavigationOnClickListener { activity.onBackPressed() }
                } else {
                    setNavigationIcon(menuResource)
                    setNavigationOnClickListener { onMenuClickObs.get()() }
                }
                setToolbarTitle(stack.getTitle(resources))
            }
        }
        doThisOnBackPressed = { stack.onBackPressed(it) }
    }

    fun attach(activity: VCActivity, stack: VCStack) {
        bind(alwaysShowBackObs) {
            toolbar?.apply {
                if (stack.size > 1 || alwaysShowBack) {
                    setNavigationIcon(backResource)
                    setNavigationOnClickListener { activity.onBackPressed() }
                } else {
                    navigationIcon = null
                    setNavigationOnClickListener { }
                }
                setToolbarTitle(stack.getTitle(resources))
            }
        }
        bind(stack.onSwap, stack.current) {
            toolbar?.apply {
                if (stack.size > 1 || alwaysShowBack) {
                    setNavigationIcon(backResource)
                    setNavigationOnClickListener { activity.onBackPressed() }
                } else {
                    navigationIcon = null
                    setNavigationOnClickListener { }
                }
                setToolbarTitle(stack.getTitle(resources))
            }
        }
        doThisOnBackPressed = { stack.onBackPressed(it) }
    }

    fun attach(activity: VCActivity, container: VCContainer) {
        bind(container.onSwap, container.current) {
            toolbar?.apply {
                setToolbarTitle(container.getTitle(resources))
            }
        }
    }

    var doThisOnBackPressed: (backAction: () -> Unit) -> Unit = { it() }
    override fun onBackPressed(backAction: () -> Unit) = doThisOnBackPressed(backAction)
}