package com.lightningkite.kotlincomponents.ui

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.lightningkite.kotlincomponents.async.doAsync
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textResource

/**
 * Created by jivie on 12/2/15.
 */
class ProgressButton(context: Context) : FrameLayout(context){

    val button:Button = Button(context)
    val progress:ProgressBar = ProgressBar(context)

    init{
        progress.visibility = View.INVISIBLE
        val params = generateDefaultLayoutParams()
        params.gravity = Gravity.CENTER
        params.width = matchParent
        params.height = matchParent
        addView(button, params)
        addView(progress, params)
    }

    var running:Boolean = false
        get() = field
        set(value){
            if(value){
                if(field) return
                field = true

                button.isEnabled = false

                progress.alpha = 0f
                progress.visibility = View.VISIBLE

                progress.animate().alpha(1f).setDuration(300).start()
                button.animate().alpha(0f).setDuration(300).start()
            } else {
                if(!field) return
                field = false

                button.isEnabled = true

                button.animate().alpha(1f).setDuration(300).start()
                progress.animate().alpha(0f).setDuration(300).withEndAction {
                    progress.visibility = View.INVISIBLE
                }.start()
            }
        }

    fun onClick(func:(View)->Unit){
        button.setOnClickListener(func)
    }
}

public inline fun ViewManager.progressButton(init: ProgressButton.() -> Unit): ProgressButton {
    return ankoView({ ProgressButton(it) }, init)
}

public inline fun ViewManager.progressButton(text:String, init: ProgressButton.() -> Unit): ProgressButton {
    return ankoView({ ProgressButton(it) }, {
        button.text = text
        init()
    })
}

public inline fun ViewManager.progressButton(textResource:Int, init: ProgressButton.() -> Unit): ProgressButton {
    return ankoView({ ProgressButton(it) }, {
        button.textResource = textResource
        init()
    })
}