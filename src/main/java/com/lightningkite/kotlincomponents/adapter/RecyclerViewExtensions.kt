package com.lightningkite.kotlincomponents.adapter

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Extensions to make RecyclerViews more bearable.
 * Created by jivie on 2/11/16.
 */
fun RecyclerView.horizontalDivider(drawable: Drawable) {
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

                val top = child.top + params.topMargin + (child.translationY + .5f).toInt() - dividerSize;

                drawable.setBounds(left, top, right, top + dividerSize);
                drawable.draw(c);

                val bottom = child.bottom - params.bottomMargin + (child.translationY + .5f).toInt();

                drawable.setBounds(left, bottom, right, bottom + dividerSize);
                drawable.draw(c);
            }
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.set(0, 0, 0, dividerSize);
        }
    })
}