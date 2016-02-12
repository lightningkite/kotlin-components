package com.lightningkite.kotlincomponents.ui

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import com.lightningkite.kotlincomponents.R
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCTabs
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.layoutInflater

/**
 * Various helper functions
 * Created by jivie on 2/9/16.
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

val View.visualTop: Int get() {
    val layoutParams = layoutParams
    return top - (
            if(layoutParams is ViewGroup.MarginLayoutParams)
                layoutParams.topMargin
            else
                0
            ) + (translationY + .5f).toInt()
};

val View.visualBottom: Int get() {
    val layoutParams = layoutParams
    return bottom + (
            if(layoutParams is ViewGroup.MarginLayoutParams)
                layoutParams.bottomMargin
            else
                0
            ) + (translationY + .5f).toInt()
};

inline fun Drawable.setBoundsCentered(centerX:Float, centerY:Float) = setBoundsCentered(centerX.toInt(), centerY.toInt())
inline fun Drawable.setBoundsCentered(centerX:Int, centerY:Int){
    val left = centerX - minimumWidth / 2
    val top = centerY - minimumHeight / 2
    setBounds(left, top, left + minimumWidth, top + minimumHeight)
}

inline fun RecyclerView.horizontalDivider(drawable: Drawable) {
    addItemDecoration(object : RecyclerView.ItemDecoration() {

        val dividerSize = drawable.intrinsicHeight.coerceAtLeast(1)

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val left = parent.paddingLeft;
            val right = parent.width - parent.paddingRight;

            val childCount = parent.childCount;
            for (i in 0..childCount - 1) {
                val child = parent.getChildAt(i);

                drawable.alpha = (child.alpha * 255).toInt()

                val params = child.layoutParams as RecyclerView.LayoutParams;

                val top = child.visualTop - dividerSize;

                drawable.setBounds(left, top, right, top + dividerSize);
                drawable.draw(c);

                val bottom = child.visualBottom;

                drawable.setBounds(left, bottom, right, bottom + dividerSize);
                drawable.draw(c);
            }
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.set(0, 0, 0, dividerSize);
        }
    })
}

//////////////////////

inline fun ViewManager.tabLayout() = tabLayout {}
inline fun ViewManager.tabLayout(init: TabLayout.() -> Unit) = ankoView({ TabLayout(it) }, init)

inline fun TabLayout.setUpWithVCTabs(vcTabs: VCTabs, crossinline onReselect: (Int) -> Unit, crossinline tabBuilder: TabLayout.Tab.(Int) -> Unit) {

    val offset = tabCount

    for (vc in vcTabs.viewControllers) {
        val tab = newTab()
        tab.text = vc.getTitle(resources)
        tab.tabBuilder(tab.position - offset)
        addTab(tab)
    }

    setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab) {
            onReselect(tab.position - offset)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {

        }

        override fun onTabSelected(tab: TabLayout.Tab) {
            vcTabs.index = tab.position - offset
        }

    })
}