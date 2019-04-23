package com.jonathas.eloi.osrswiki.service

import android.annotation.TargetApi
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.webkit.WebView
import android.view.*
import com.jonathas.eloi.osrswiki.MainActivity
import com.jonathas.eloi.osrswiki.R


class FloatingViewService : Service(), View.OnClickListener {

    private var url = "https://oldschool.runescape.wiki/"

    private var mWindowManager: WindowManager? = null
    private var mFloatingView: View? = null
    private var collapsedView: View? = null
    private var expandedView: View? = null

    private var initialX: Int = 0
    private var initialY: Int = 0
    private var initialTouchX: Float = 0.toFloat()
    private var initialTouchY: Float = 0.toFloat()

    private val isViewCollapsed: Boolean
        get() = mFloatingView == null || mFloatingView!!.findViewById<View>(R.id.layoutCollapsed).visibility == View.VISIBLE

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        this.url = (intent.extras!!.get("url") as String).toString()

        val webView = mFloatingView!!.findViewById<WebView>(R.id.WVsite)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
        webView.setOnKeyListener { _, keyCode, _ ->
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    }
                     true
                }
                else -> true
            }
        }

        return startId
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(applicationContext).inflate(R.layout.layout_floating_widget, null)

        //setting the layout parameters
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        //getting windows services and adding the floating view to it
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (mWindowManager != null) {
            mWindowManager!!.addView(mFloatingView, params)
        }

        //getting the collapsed and expanded view from the floating view
        collapsedView = mFloatingView!!.findViewById(R.id.layoutCollapsed)
        expandedView = mFloatingView!!.findViewById(R.id.layoutExpanded)

        //adding click listener to close button and expanded view
        mFloatingView!!.findViewById<View>(R.id.buttonClose).setOnClickListener(this)
        expandedView!!.setOnClickListener(this)

        //adding an touchlistener to make drag movement of the floating widget
        mFloatingView!!.findViewById<View>(R.id.relativeLayoutParent).setOnTouchListener(object : View.OnTouchListener {


            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        mWindowManager!!.updateViewLayout(mFloatingView, params)
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        //when the drag is ended switching the state of the widget
                        val Xdiff = (event.rawX - initialTouchX).toInt()
                        val Ydiff = (event.rawY - initialTouchY).toInt()
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed) {
                                collapsedView!!.visibility = View.GONE
                                expandedView!!.visibility = View.VISIBLE
                                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                mWindowManager!!.updateViewLayout(mFloatingView, params)
                            }
                        }
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        //this code is helping the widget to move around the screen with fingers
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        mWindowManager!!.updateViewLayout(mFloatingView, params)
                        return true
                    }
                }
                return false
            }
        })

        val webView = mFloatingView!!.findViewById<WebView>(R.id.WVsite)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mFloatingView != null) mWindowManager!!.removeView(mFloatingView)
    }

    override fun onClick(v: View) {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        when (v.id) {
            R.id.layoutExpanded -> {
                //switching views
                collapsedView!!.visibility = View.VISIBLE
                expandedView!!.visibility = View.GONE

                params.x = initialX
                params.y = initialY

                params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                mWindowManager!!.updateViewLayout(mFloatingView, params)
            }

            R.id.buttonClose ->
                //closing the widget
                stopSelf()
        }
    }
}