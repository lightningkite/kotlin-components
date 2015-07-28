package com.lightningkite.kotlincomponents.viewcontroller

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lightningkite.kotlincomponents.getActivity
import org.jetbrains.anko.layoutParams
import java.util.Stack

/**
 * Created by jivie on 7/24/15.
 */
public class ViewControllerView(activity: Activity, startVC: ViewController) : FrameLayout(activity), ViewControllerStack {

    public val stack: Stack<ViewController> = Stack()
    public val activity: Activity = activity
    public var currentView: View? = null

    init {
        pushView(startVC)
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superstate", super<FrameLayout>.onSaveInstanceState())
        stack.saveState(bundle, "stack")
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null) return
        val bundle = state as Bundle
        super<FrameLayout>.onRestoreInstanceState(bundle.getParcelable<Parcelable>("superstate"))
        stack.loadState(bundle, "stack")
        currentView = stack.peek().make(activity, this)
        addView(currentView)
    }

    override fun pushView(newController: ViewController) {
        val oldController = if (stack.size() > 0) stack.peek() else null
        stack.push(newController)

        switchView(oldController, newController)//, popOutTransition, popInTransition)
    }

    override fun popView() {
        if (stack.size() <= 1) return
        val oldController = stack.pop()
        if (currentView != null) {
            oldController.dispose(currentView!!)
        }
        val newController = stack.last()

        switchView(oldController, newController)//, popOutTransition, popInTransition)
    }

    protected fun switchView(
            oldController: ViewController?,
            newController: ViewController) {

        val newView = newController.make(activity, this)
        newView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        if (currentView != null && oldController != null) {
            removeView(currentView)
            oldController.dispose(currentView!!)
        }

        addView(newView)
        currentView = newView
    }

    override fun optView(tag: String): ViewController? {
        var returnVal: ViewController? = null
        for (controller in stack) {
            if (controller.tag.equals(tag)) returnVal = controller
        }
        return returnVal
    }

    private var onResultLambda: (result: Int, data: Intent?) -> Unit = { result, data -> }
    override fun startIntent(intent: Intent, onResult: (result: Int, data: Intent?) -> Unit, options: Bundle) {
        onResultLambda = onResult
        getActivity()?.startActivityForResult(intent, 0, options)
    }

    //Users of the class are responsible for calling this!
    public fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onResultLambda(resultCode, data)
    }
}