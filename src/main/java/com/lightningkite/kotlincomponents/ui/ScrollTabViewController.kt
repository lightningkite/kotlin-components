package com.lightningkite.kotlincomponents.ui

import android.content.Context
import android.view.View
import android.widget.ListAdapter
import com.lightningkite.kotlincomponents.databinding.Bond
import com.lightningkite.kotlincomponents.databinding.BondSet
import com.lightningkite.kotlincomponents.horizontal
import com.lightningkite.kotlincomponents.run
import com.lightningkite.kotlincomponents.vertical
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import com.lightningkite.kotlincomponents.viewcontroller.ViewControllerStack
import org.jetbrains.anko.*

/**
 * Created by jivie on 9/4/15.
 */
public class ScrollTabViewController(val tabAdapter: ListAdapter, val initialTab: Int = 0, val tabMaker: (Int) -> ViewController) : ViewController {

    public val bondSet: BondSet = BondSet()

    public val currentVcBond: Bond<ViewController> = bondSet.make(tabMaker(initialTab))
    public var currentVc: ViewController by currentVcBond

    override fun make(context: Context, stack: ViewControllerStack): View = _LinearLayout(context).run {
        orientation = vertical

        scrollView {
            adapterLinearLayout {
                orientation = horizontal
                adapter = tabAdapter
                onItemClick { lv, v, p, id ->
                    currentVc = tabMaker(p)
                }
            }
        }.layoutParams(matchParent, wrapContent)

        frameLayout {
            var frameView: View? = null
            var frameVc: ViewController? = null

            currentVcBond.bind {
                //Remove the previous view if it exists
                removeAllViews()
                frameVc?.dispose(frameView!!)

                //Add the new view
                frameVc = it
                frameView = it.make(context, stack)
                addView(frameView)
                frameView!!.layoutParams(matchParent, matchParent)
            }
        }.layoutParams(matchParent, 0, 1f)
    }
}