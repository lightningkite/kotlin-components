package com.lightningkite.kotlincomponents.ui

import android.view.View
import android.widget.LinearLayout
import com.lightningkite.kotlincomponents.adapter.AdaptableViewController
import com.lightningkite.kotlincomponents.adapter.AdaptableViewControllerImpl
import com.lightningkite.kotlincomponents.adapter.ViewControllerAdapter
import com.lightningkite.kotlincomponents.animation.AnimationSet
import com.lightningkite.kotlincomponents.databinding.Bond
import com.lightningkite.kotlincomponents.viewcontroller.AutocleanViewController
import com.lightningkite.kotlincomponents.viewcontroller.ViewController
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCContainer
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import org.jetbrains.anko.onClick
import java.util.*

/**
 * Created by jivie on 10/14/15.

class Tabs<T>(
        val tabs: List<Pair<T, ViewController>>,
        startTab:Pair<T, ViewController> = tabs[0],
        val orientation:Int = LinearLayout.HORIZONTAL,
        val stretch:Boolean = true,
        val style:AdapterLinearLayout.()->Unit = {},
        val makeFunction: Tabs.TabVC<T>.()->View

): AutocleanViewController(), VCContainer {

    public val selectedBond: Bond<Pair<T, ViewController>> = listener(Bond(startTab))
    public var selected:Pair<T, ViewController> by selectedBond

    override val current: ViewController = selected.second

    override var swapListener: ((ViewController, AnimationSet?) -> Unit)? = null
    override var onSwapCompleteListeners: ArrayList<() -> Unit> = ArrayList()

    override fun make(activity: VCActivity): View {
        return AdapterLinearLayout(activity, stretch).apply{
            this.orientation = this@Tabs.orientation
            adapter = ViewControllerAdapter(activity, tabs){

                object : AdaptableViewControllerImpl<T>(it) {
                }

            }
        }
    }

    override fun dispose() {

    }

    inner class TabVC<T>(initial: Pair<T, ViewController>): AdaptableViewController<Pair<T, ViewController>>{
        override var itemBond: Bond<Pair<T, ViewController>> = Bond(initial)
        override var item: Pair<T, ViewController>
            get() = itemBond.get()
            set(value) = itemBond.set(value)

        val display: T get() = item.first
        val controller: ViewController get() = item.second
        val isSelected: Boolean get() = selected == item

        override fun unmake(view: View) {
            itemBond.dispose()
        }

        override fun make(activity: VCActivity): View {
            val view = this.makeFunction()
            view.onClick{
                selected = item
            }
        }
    }

}*/