package com.lightningkite.kotlincomponents.ui

import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewManager
import com.lightningkite.kotlincomponents.viewcontroller.containers.VCTabs
import org.jetbrains.anko.custom.ankoView

/**
 * Created by jivie on 2/9/16.
 */

inline fun ViewManager.recyclerView() = recyclerView {}
inline fun ViewManager.recyclerView(init: RecyclerView.() -> Unit) = ankoView({ RecyclerView(it) }, init)

inline fun ViewManager.verticalRecyclerView() = verticalRecyclerView {}
inline fun ViewManager.verticalRecyclerView(init: RecyclerView.() -> Unit) = ankoView({
    RecyclerView(it).apply {
        layoutManager = LinearLayoutManager(it).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }
    }
}, init)

inline fun ViewManager.horizontalRecyclerView() = horizontalRecyclerView() {}
inline fun ViewManager.horizontalRecyclerView(init: RecyclerView.() -> Unit) = ankoView({
    RecyclerView(it).apply {
        layoutManager = LinearLayoutManager(it).apply {
            this.orientation = LinearLayoutManager.HORIZONTAL
        }
    }
}, init)

inline fun ViewManager.tabLayout() = tabLayout {}
inline fun ViewManager.tabLayout(init: TabLayout.() -> Unit) = ankoView({ TabLayout(it) }, init)

inline fun TabLayout.setUpWithVCTabs(vcTabs: VCTabs, crossinline onReselect: (Int) -> Unit, crossinline tabBuilder: TabLayout.Tab.(Int) -> Unit) {

    val offset = tabCount

    for (vc in vcTabs.viewControllers) {
        val tab = newTab()
        tab.text = vc.getTitle(resources)
        tab.tabBuilder(tab.position - offset)
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