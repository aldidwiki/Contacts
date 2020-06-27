package com.aldidwikip.contacts.utils

import android.view.View
import android.widget.FrameLayout
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.style.Circle
import kotlin.properties.Delegates

class SpinKitLoadingAnimation(private val mSpinKitView: SpinKitView, private val mFLSpinKit: FrameLayout) {

    private var mEnabled by Delegates.notNull<Boolean>()

    fun show(enabled: Boolean) {
        val progressBar = mSpinKitView
        val circle = Circle()
        progressBar.setIndeterminateDrawable(circle)

        if (enabled) {
            mSpinKitView.visibility = View.VISIBLE
            mFLSpinKit.visibility = View.VISIBLE
            mEnabled = true
        } else {
            mSpinKitView.visibility = View.GONE
            mFLSpinKit.visibility = View.GONE
            mEnabled = false
        }
    }

    fun isEnabled() = mEnabled
}