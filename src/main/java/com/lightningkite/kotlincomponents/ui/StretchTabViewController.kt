package com.lightningkite.kotlincomponents.ui

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ListAdapter
import com.lightningkite.kotlincomponents.addView
import com.lightningkite.kotlincomponents.databinding.Bond
import com.lightningkite.kotlincomponents.databinding.BondSet
import com.lightningkite.kotlincomponents.horizontal
import com.lightningkite.kotlincomponents.vertical
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import com.lightningkite.kotlincomponents.viewcontroller.ViewControllerStack
import org.jetbrains.anko.*

/**
 * Created by jivie on 9/4/15.
 */
public class StretchTabViewController(val tabAdapter: ListAdapter, val initialTab: Int = 0, val tabMaker: (Int) -> ViewController) : ViewController {

    public val bondSet: BondSet = BondSet()

    public val currentVcBond: Bond<ViewController> = bondSet.make(tabMaker(initialTab))
    public var currentVc: ViewController by currentVcBond

    override fun make(context: Context, stack: ViewControllerStack): View = _LinearLayout(context).run {
        orientation = vertical

        linearLayout {
            orientation = horizontal
            gravity = Gravity.BOTTOM
            for (i in 0..tabAdapter.count - 1) {
                addView(tabAdapter.getView(i, null, this)) {
                    onClick {
                        currentVc = tabMaker(i)
                    }
                }.lparams(0, wrapContent, 1f)
            }
        }.lparams(matchParent, wrapContent)

        frameLayout {
            var frameView: View? = null
            var frameVc: ViewController? = null

            currentVcBond.bind {
                //TODO: Add animations to the change.

                //Remove the previous view if it exists
                removeAllViews()
                frameVc?.dispose(frameView!!)

                //Add the new view
                frameVc = it
                frameView = it.make(context, stack)
                addView(frameView)
                frameView!!.lparams(matchParent, matchParent)
            }
        }.lparams(matchParent, 0, 1f)
    }
}