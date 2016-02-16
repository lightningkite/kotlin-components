package com.lightningkite.kotlincomponents.ui

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.design.widget.TabLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlincomponents.versionOn
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCTabs

/**
 * Various helper functions
 * Created by jivie on 2/9/16.
 */

var View.elevationCompat: Float
    get() {
        versionOn(Build.VERSION_CODES.LOLLIPOP) {
            return elevation
        }
        return 0f
    }
    set(value) {
        versionOn(Build.VERSION_CODES.LOLLIPOP) {
            elevation = value
        }
    }

val View.visualTop: Int get() {
    val layoutParams = layoutParams
    return top - (
            if (layoutParams is ViewGroup.MarginLayoutParams)
                layoutParams.topMargin
            else
                0
            ) + (translationY + .5f).toInt()
};

val View.visualBottom: Int get() {
    val layoutParams = layoutParams
    return bottom + (
            if (layoutParams is ViewGroup.MarginLayoutParams)
                layoutParams.bottomMargin
            else
                0
            ) + (translationY + .5f).toInt()
};

inline fun Drawable.setBoundsCentered(centerX: Float, centerY: Float) = setBoundsCentered(centerX.toInt(), centerY.toInt())
inline fun Drawable.setBoundsCentered(centerX: Int, centerY: Int) {
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

inline fun TabLayout.setUpWithVCTabs(vcTabs: VCTabs, crossinline onReselect: (Int) -> Unit, crossinline tabBuilder: TabLayout.Tab.(Int) -> Unit) {

    val offset = tabCount

    var index = 0

    for (vc in vcTabs.viewControllers) {
        val tab = newTab()
        tab.text = vc.getTitle(resources)
        tab.tabBuilder(index - offset)
        addTab(tab)
        index++
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