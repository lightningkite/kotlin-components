package com.lightningkite.kotlincomponents.adapter

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.lightningkite.kotlincomponents.alpha

/**
 * Created by jivie on 2/11/16.
 */
open class ActionItemTouchHelperListener(
        val leftAction: SwipeAction? = null,
        val rightAction: SwipeAction? = null,
        val drawablePadding: Int = 0
) : ItemTouchHelper.Callback() {

    data class SwipeAction(val color: Int, val drawable: Drawable, val canDo: (Int) -> Boolean, val action: (Int) -> Unit)

    override fun getMovementFlags(p0: RecyclerView?, p1: RecyclerView.ViewHolder?): Int {
        var swipeDirections = 0
        if (leftAction != null) swipeDirections = swipeDirections or ItemTouchHelper.LEFT
        if (rightAction != null) swipeDirections = swipeDirections or ItemTouchHelper.RIGHT
        return makeMovementFlags(0, swipeDirections)
    }

    override fun onMove(p0: RecyclerView?, p1: RecyclerView.ViewHolder?, p2: RecyclerView.ViewHolder?): Boolean = false

    @Suppress("UNCHECKED_CAST")
    override fun onSwiped(holder: RecyclerView.ViewHolder, swipeDirection: Int) {
        if (swipeDirection == ItemTouchHelper.LEFT) {
            leftAction?.action?.invoke(holder.adapterPosition)
        }
        if (swipeDirection == ItemTouchHelper.RIGHT) {
            rightAction?.action?.invoke(holder.adapterPosition)
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            val itemView = viewHolder.itemView;

            if (dX > 0) {
                val rightAction = rightAction
                if (rightAction != null) {
                    val icon = rightAction.drawable
                    // swiping right
                    val top = (itemView.height / 2) - (icon.minimumHeight / 2);
                    val left = (itemView.x.toInt() / 2) - (icon.minimumWidth / 2);
                    val ratio = 1 - dX / itemView.width

                    c.save()
                    c.translate(recyclerView.x, itemView.y)
                    c.clipRect(itemView.left.toFloat(), 0f, dX, itemView.height.toFloat())

                    c.drawColor(rightAction.color.alpha(ratio))
                    icon.setBounds(left, top, left + icon.minimumWidth, top + icon.minimumHeight)
                    icon.alpha = (ratio * 255).toInt()
                    icon.draw(c)

                    c.restore()
                }

            } else if (dX < 0) {
                val leftAction = leftAction
                if (leftAction != null) {
                    val icon = leftAction.drawable
                    // swiping right
                    val top = (itemView.height / 2) - (icon.minimumHeight / 2);
                    val left = ((recyclerView.width * 2 + dX.toInt()) / 2) - (icon.minimumWidth / 2);
                    val ratio = 1 + dX / itemView.width

                    c.save()
                    c.translate(recyclerView.x, itemView.y)
                    c.clipRect(recyclerView.right.toFloat() + dX, 0f, recyclerView.right.toFloat(), itemView.height.toFloat())

                    c.drawColor(leftAction.color.alpha(ratio))
                    icon.setBounds(left, top, left + icon.minimumWidth, top + icon.minimumHeight)
                    icon.alpha = (ratio * 255).toInt()
                    icon.draw(c)

                    c.restore()
                }

            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


        }
    }
}

fun RecyclerView.swipe(leftAction: ActionItemTouchHelperListener.SwipeAction?, rightAction: ActionItemTouchHelperListener.SwipeAction?, padding: Int) {
    val listener = ActionItemTouchHelperListener(leftAction, rightAction, padding)
    ItemTouchHelper(listener).attachToRecyclerView(this)
}