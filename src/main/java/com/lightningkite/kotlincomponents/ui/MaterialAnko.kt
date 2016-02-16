package com.lightningkite.kotlincomponents.ui

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.ActionMenuView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewManager
import com.lightningkite.kotlincomponents.R
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.layoutInflater

/**
 * Anko stuff for Material design
 * Created by josep on 2/12/2016.
 */

inline fun ViewManager.recyclerView() = recyclerView {}

inline fun ViewManager.recyclerView(init: RecyclerView.() -> Unit) = ankoView({ RecyclerView(it) }, init)

inline fun ViewManager.verticalRecyclerView() = verticalRecyclerView {}
inline fun ViewManager.verticalRecyclerView(init: RecyclerView.() -> Unit) = ankoView({
    val view = it.layoutInflater.inflate(R.layout.vertical_recycler_view, null) as RecyclerView
    view.apply {
        layoutManager = LinearLayoutManager(it).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }
    }
}, init)

inline fun ViewManager.horizontalRecyclerView() = horizontalRecyclerView() {}
inline fun ViewManager.horizontalRecyclerView(init: RecyclerView.() -> Unit) = ankoView({
    val view = it.layoutInflater.inflate(R.layout.horizontal_recycler_view, null) as RecyclerView
    view.apply {
        layoutManager = LinearLayoutManager(it).apply {
            this.orientation = LinearLayoutManager.HORIZONTAL
        }
    }
}, init)

inline fun ViewManager.tabLayout() = tabLayout {}
inline fun ViewManager.tabLayout(init: TabLayout.() -> Unit) = ankoView({ TabLayout(it) }, init)

class _Toolbar(context: Context) : Toolbar(context) {
    inline fun <T : View> T.lparams(gravity: Int): T {
        layoutParams = Toolbar.LayoutParams(gravity)
        return this
    }

    inline fun <T : View> T.lparams(width: Int, height: Int, gravity: Int): T {
        layoutParams = Toolbar.LayoutParams(width, height, gravity)
        return this
    }
}

inline fun ViewManager.toolBar() = toolBar {}
inline fun ViewManager.toolBar(init: _Toolbar.() -> Unit) = ankoView({ _Toolbar(it) }, init)

inline fun ViewManager.actionMenuView() = actionMenuView {}
inline fun ViewManager.actionMenuView(init: ActionMenuView.() -> Unit) = ankoView({ ActionMenuView(it) }, init)

class _CoordinatorLayout(context: Context) : CoordinatorLayout(context) {

    inline fun <T : View> T.lparams(width: Int, height: Int): T {
        val newParams = CoordinatorLayout.LayoutParams(width, height)
        layoutParams = newParams
        return this
    }

    inline fun <T : View> T.lparams(width: Int, height: Int, init: CoordinatorLayout.LayoutParams.() -> Unit): T {
        val newParams = CoordinatorLayout.LayoutParams(width, height)
        newParams.init()
        layoutParams = newParams
        return this
    }
}

inline fun ViewManager.coordinatorLayout() = coordinatorLayout {}
inline fun ViewManager.coordinatorLayout(init: _CoordinatorLayout.() -> Unit) = ankoView({ _CoordinatorLayout(it) }, init)

inline fun ViewManager.textInputLayout() = tabLayout {}
inline fun ViewManager.textInputLayout(init: TextInputLayout.() -> Unit) = ankoView({ TextInputLayout(it) }, init)
inline fun ViewManager.textInputLayout(hint: String, init: TextInputLayout.() -> Unit) = ankoView({
    TextInputLayout(it).apply {
        this.hint = hint
    }
}, init)

inline fun ViewManager.textInputLayout(hint: Int, init: TextInputLayout.() -> Unit) = ankoView({
    TextInputLayout(it).apply {
        this.hint = it.getString(hint)
    }
}, init)

fun View.snackbar(text: CharSequence, duration: Int = Snackbar.LENGTH_SHORT, init: Snackbar.() -> Unit = {}): Snackbar {
    val snack = Snackbar.make(this, text, duration)
    snack.init()
    snack.show()
    return snack
}

fun View.snackbar(text: Int, duration: Int = Snackbar.LENGTH_SHORT, init: Snackbar.() -> Unit = {}): Snackbar {
    val snack = Snackbar.make(this, text, duration)
    snack.init()
    snack.show()
    return snack
}

