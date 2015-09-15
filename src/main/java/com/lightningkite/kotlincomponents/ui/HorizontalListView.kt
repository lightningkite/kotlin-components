/*
 * HorizontalListView.java v1.5
 *
 * 
 * The MIT License
 * Copyright (c) 2011 Paul Soucy (paul@dev-smart.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.lightningkite.kotlincomponents.ui

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListAdapter
import android.widget.Scroller
import java.util.LinkedList
import kotlin.properties.Delegates

public class HorizontalListView(context: Context, attrs: AttributeSet) : AdapterView<ListAdapter>(context, attrs) {


    public var mAlwaysOverrideTouch: Boolean = true
    protected var mAdapter: ListAdapter? = null
    private var mLeftViewIndex = -1
    private var mRightViewIndex = 0
    protected var mCurrentX: Int = 0
    protected var mNextX: Int = 0
    private var mMaxX = Integer.MAX_VALUE
    private var mDisplayOffset = 0
    protected var mScroller: Scroller by Delegates.notNull()
    private var mGesture: GestureDetector? = null
    private val mRemovedViewQueue = LinkedList<View>()
    private var mOnItemSelected: AdapterView.OnItemSelectedListener? = null
    private var mOnItemClicked: AdapterView.OnItemClickListener? = null
    private var mOnItemLongClicked: AdapterView.OnItemLongClickListener? = null
    private var mDataChanged = false


    init {
        initView()
    }

    synchronized private fun initView() {
        mLeftViewIndex = -1
        mRightViewIndex = 0
        mDisplayOffset = 0
        mCurrentX = 0
        mNextX = 0
        mMaxX = Integer.MAX_VALUE
        mScroller = Scroller(getContext())
        mGesture = GestureDetector(getContext(), mOnGesture)
    }

    override fun setOnItemSelectedListener(listener: AdapterView.OnItemSelectedListener?) {
        mOnItemSelected = listener
    }

    override fun setOnItemClickListener(listener: AdapterView.OnItemClickListener?) {
        mOnItemClicked = listener
    }

    override fun setOnItemLongClickListener(listener: AdapterView.OnItemLongClickListener) {
        mOnItemLongClicked = listener
    }

    private val mDataObserver = object : DataSetObserver() {

        override fun onChanged() {
            synchronized (this@HorizontalListView) {
                mDataChanged = true
            }
            invalidate()
            requestLayout()
        }

        override fun onInvalidated() {
            reset()
            invalidate()
            requestLayout()
        }

    }

    override fun getAdapter(): ListAdapter? {
        return mAdapter
    }

    override fun getSelectedView(): View? {
        //TODO: implement
        return null
    }

    override fun setAdapter(adapter: ListAdapter?) {
        if (mAdapter != null) {
            mAdapter!!.unregisterDataSetObserver(mDataObserver)
        }
        mAdapter = adapter
        mAdapter!!.registerDataSetObserver(mDataObserver)
        reset()
    }

    synchronized private fun reset() {
        initView()
        removeAllViewsInLayout()
        requestLayout()
    }

    override fun setSelection(position: Int) {
        //TODO: implement
    }

    private fun addAndMeasureChild(child: View, viewPos: Int) {
        var params: ViewGroup.LayoutParams? = child.getLayoutParams()
        if (params == null) {
            params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        addViewInLayout(child, viewPos, params, true)
        child.measure(View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.AT_MOST))
    }


    synchronized override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (mAdapter == null) {
            return
        }

        if (mDataChanged) {
            val oldCurrentX = mCurrentX
            initView()
            removeAllViewsInLayout()
            mNextX = oldCurrentX
            mDataChanged = false
        }

        if (mScroller.computeScrollOffset()) {
            val scrollx = mScroller.getCurrX()
            mNextX = scrollx
        }

        if (mNextX <= 0) {
            mNextX = 0
            mScroller.forceFinished(true)
        }
        if (mNextX >= mMaxX) {
            mNextX = mMaxX
            mScroller.forceFinished(true)
        }

        val dx = mCurrentX - mNextX

        removeNonVisibleItems(dx)
        fillList(dx)
        positionItems(dx)

        mCurrentX = mNextX

        if (!mScroller.isFinished()) {
            post(object : Runnable {
                override fun run() {
                    requestLayout()
                }
            })

        }
    }

    private fun fillList(dx: Int) {
        var edge = 0
        var child: View? = getChildAt(getChildCount() - 1)
        if (child != null) {
            edge = child.getRight()
        }
        fillListRight(edge, dx)

        edge = 0
        child = getChildAt(0)
        if (child != null) {
            edge = child.getLeft()
        }
        fillListLeft(edge, dx)


    }

    private fun fillListRight(rightEdge: Int, dx: Int) {
        var rightEdge = rightEdge
        while (rightEdge + dx < getWidth() && mRightViewIndex < mAdapter!!.getCount()) {

            val child = mAdapter!!.getView(mRightViewIndex, mRemovedViewQueue.poll(), this)
            addAndMeasureChild(child, -1)
            rightEdge += child.getMeasuredWidth()

            if (mRightViewIndex == mAdapter!!.getCount() - 1) {
                mMaxX = mCurrentX + rightEdge - getWidth()
            }

            if (mMaxX < 0) {
                mMaxX = 0
            }
            mRightViewIndex++
        }

    }

    private fun fillListLeft(leftEdge: Int, dx: Int) {
        var leftEdge = leftEdge
        while (leftEdge + dx > 0 && mLeftViewIndex >= 0) {
            val child = mAdapter!!.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this)
            addAndMeasureChild(child, 0)
            leftEdge -= child.getMeasuredWidth()
            mLeftViewIndex--
            mDisplayOffset -= child.getMeasuredWidth()
        }
    }

    private fun removeNonVisibleItems(dx: Int) {
        var child: View? = getChildAt(0)
        while (child != null && child.getRight() + dx <= 0) {
            mDisplayOffset += child.getMeasuredWidth()
            mRemovedViewQueue.offer(child)
            removeViewInLayout(child)
            mLeftViewIndex++
            child = getChildAt(0)

        }

        child = getChildAt(getChildCount() - 1)
        while (child != null && child.getLeft() + dx >= getWidth()) {
            mRemovedViewQueue.offer(child)
            removeViewInLayout(child)
            mRightViewIndex--
            child = getChildAt(getChildCount() - 1)
        }
    }

    private fun positionItems(dx: Int) {
        if (getChildCount() > 0) {
            mDisplayOffset += dx
            var left = mDisplayOffset
            for (i in 0..getChildCount() - 1) {
                val child = getChildAt(i)
                val childWidth = child.getMeasuredWidth()
                child.layout(left, 0, left + childWidth, child.getMeasuredHeight())
                left += childWidth + child.getPaddingRight()
            }
        }
    }

    synchronized public fun scrollTo(x: Int) {
        mScroller.startScroll(mNextX, 0, x - mNextX, 0)
        requestLayout()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        var handled = super.dispatchTouchEvent(ev)
        handled = handled or mGesture!!.onTouchEvent(ev)
        return handled
    }

    protected fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        synchronized (this@HorizontalListView) {
            mScroller.fling(mNextX, 0, (-velocityX).toInt(), 0, 0, mMaxX, 0, 0)
        }
        requestLayout()

        return true
    }

    protected fun onDown(e: MotionEvent): Boolean {
        mScroller.forceFinished(true)
        return true
    }

    private val mOnGesture = object : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return this@HorizontalListView.onDown(e)
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            return this@HorizontalListView.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {

            synchronized (this@HorizontalListView) {
                mNextX += distanceX.toInt()
            }
            requestLayout()

            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            for (i in 0..getChildCount() - 1) {
                val child = getChildAt(i)
                if (isEventWithinView(e, child)) {
                    if (mOnItemClicked != null) {
                        mOnItemClicked!!.onItemClick(this@HorizontalListView, child, mLeftViewIndex + 1 + i, mAdapter!!.getItemId(mLeftViewIndex + 1 + i))
                    }
                    if (mOnItemSelected != null) {
                        mOnItemSelected!!.onItemSelected(this@HorizontalListView, child, mLeftViewIndex + 1 + i, mAdapter!!.getItemId(mLeftViewIndex + 1 + i))
                    }
                    break
                }

            }
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            val childCount = getChildCount()
            for (i in 0..childCount - 1) {
                val child = getChildAt(i)
                if (isEventWithinView(e, child)) {
                    if (mOnItemLongClicked != null) {
                        mOnItemLongClicked!!.onItemLongClick(this@HorizontalListView, child, mLeftViewIndex + 1 + i, mAdapter!!.getItemId(mLeftViewIndex + 1 + i))
                    }
                    break
                }

            }
        }

        private fun isEventWithinView(e: MotionEvent, child: View): Boolean {
            val viewRect = Rect()
            val childPosition = IntArray(2)
            child.getLocationOnScreen(childPosition)
            val left = childPosition[0]
            val right = left + child.getWidth()
            val top = childPosition[1]
            val bottom = top + child.getHeight()
            viewRect.set(left, top, right, bottom)
            return viewRect.contains(e.getRawX().toInt(), e.getRawY().toInt())
        }
    }


}


public var HorizontalListView.adapter: ListAdapter?
    get() = getAdapter()
    set(value) {
        setAdapter(value)
    }