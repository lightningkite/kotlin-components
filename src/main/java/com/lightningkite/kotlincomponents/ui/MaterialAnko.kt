package com.lightningkite.kotlincomponents.ui

import android.content.Context
import android.support.design.widget.*
import android.support.v7.widget.*
import android.view.View
import android.view.ViewManager
import android.widget.FrameLayout
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

inline fun ViewManager.verticalGridRecyclerView(spanCount: Int) = verticalGridRecyclerView(spanCount) {}
inline fun ViewManager.verticalGridRecyclerView(spanCount: Int, init: RecyclerView.() -> Unit) = ankoView({
    val view = it.layoutInflater.inflate(R.layout.vertical_recycler_view, null) as RecyclerView
    view.apply {
        layoutManager = GridLayoutManager(it, spanCount).apply {
            this.orientation = GridLayoutManager.VERTICAL
        }
    }
}, init)

inline fun ViewManager.horizontalGridRecyclerView(spanCount: Int) = horizontalGridRecyclerView(spanCount) {}
inline fun ViewManager.horizontalGridRecyclerView(spanCount: Int, init: RecyclerView.() -> Unit) = ankoView({
    val view = it.layoutInflater.inflate(R.layout.horizontal_recycler_view, null) as RecyclerView
    view.apply {
        layoutManager = GridLayoutManager(it, spanCount).apply {
            this.orientation = GridLayoutManager.HORIZONTAL
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

class _CardView(context: Context) : CardView(context) {

    inline fun <T : View> T.lparams(width: Int, height: Int): T {
        val newParams = FrameLayout.LayoutParams(width, height)
        layoutParams = newParams
        return this
    }

    inline fun <T : View> T.lparams(width: Int, height: Int, init: FrameLayout.LayoutParams.() -> Unit): T {
        val newParams = FrameLayout.LayoutParams(width, height)
        newParams.init()
        layoutParams = newParams
        return this
    }
}

inline fun ViewManager.cardView() = cardView {}
inline fun ViewManager.cardView(init: _CardView.() -> Unit) = ankoView({ _CardView(it) }, init)

inline fun ViewManager.textInputLayout() = textInputLayout {}
inline fun ViewManager.textInputLayout(init: TextInputLayout.() -> Unit) = ankoView({ TextInputLayout(it) }, init)
inline fun ViewManager.textInputLayout(hint: String, init: TextInputLayout.() -> Unit) = ankoView({
    TextInputLayout(it).apply {
        this.hint = hint
    }
}, init)

inline fun ViewManager.textInputEditText() = textInputEditText {}
inline fun ViewManager.textInputEditText(init: TextInputEditText.() -> Unit) = ankoView({ TextInputEditText(it) }, init)

var TextInputLayout.errorResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        error = resources.getString(value)
    }

inline fun ViewManager.textInputLayout(hint: Int, init: TextInputLayout.() -> Unit) = ankoView({
    TextInputLayout(it).apply {
        this.hint = it.getString(hint)
    }
}, init)

fun View.snackbar(text: CharSequence, duration: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {}) {
    val snack = Snackbar.make(this, text, duration)
    snack.init()
    snack.show()
}

fun View.snackbar(text: Int, duration: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {}) {
    val snack = Snackbar.make(this, text, duration)
    snack.init()
    snack.show()
}

