package com.tian.jelajah.base

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.SpriteFactory
import com.github.ybq.android.spinkit.Style
import com.tian.jelajah.R
import com.tian.jelajah.utils.alert
import com.tian.jelajah.utils.isNetworkAvailable

abstract class BaseActivty : AppCompatActivity() {

    private var spinView : SpinKitView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(savedInstanceState)
        setupProgressDialog()
        if (!isNetworkAvailable()) alert("Notification", "Please Turn on your network") { positiveButton("OK") {} }.show()
    }

    abstract fun initData(savedInstanceState : Bundle?)

    fun showProgressDialog(){spinView?.visibility = View.VISIBLE}

    fun hideProgressDialog(){spinView?.visibility = View.GONE}



    private fun setupProgressDialog(){
        spinView = SpinKitView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        val style: Style = Style.values()[2]
        val drawable = SpriteFactory.create(style)
        spinView?.setIndeterminateDrawable(drawable)
        spinView?.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_active))
        val root = this.findViewById<ViewGroup>(android.R.id.content)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER }
        root.addView(FrameLayout(this).apply {
            layoutParams = params
            addView(spinView)
        })
        spinView?.visibility = View.GONE
    }
}