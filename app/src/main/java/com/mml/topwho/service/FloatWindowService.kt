package com.mml.topwho.service

import android.app.Service
import android.view.MotionEvent
import android.content.Intent
import android.os.IBinder
import android.view.WindowManager
import android.view.Gravity
import android.graphics.PixelFormat
import android.os.Build
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import kotlin.properties.Delegates


class FloatWindowService : Service() {
    fun logi(msg: String) {
        Log.i(FloatWindowService::class.java.simpleName, msg)
    }

    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null

    private var button: Button? = null

    override fun onCreate() {
        logi("onCreate")
        super.onCreate()
        isStarted = true
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager?
        layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layoutParams!!.format = PixelFormat.RGBA_8888
        layoutParams!!.gravity = Gravity.LEFT or Gravity.TOP
        layoutParams!!.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams!!.width = 500
        layoutParams!!.height = 200
        layoutParams!!.x = 300
        layoutParams!!.y = 300
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        logi("onDestroy")
        isStarted = false
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        logi("onStartCommand")
        showFloatingWindow()
        instances = this
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showFloatingWindow() {
        button = Button(applicationContext)
        button!!.apply {
            text = "TopWho Window"
            setBackgroundColor(Color.TRANSPARENT)
            textSize = 10f
            isAllCaps = false
            FloatingListener().let {
                setOnTouchListener(it)
                setOnClickListener(it)
            }
        }
        windowManager!!.addView(button, layoutParams)
    }

    private fun removeView() {
        windowManager!!.removeView(button)
    }

    private fun addView() {
        windowManager!!.addView(button, layoutParams)
    }

    private inner class FloatingListener : View.OnTouchListener, View.OnClickListener {
        override fun onClick(p0: View?) {
            logi("onClick")
//            val accessibleIntent =  Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
//            startActivity(accessibleIntent)
        }

        private var x: Int = 0
        private var y: Int = 0

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            logi("onTouch")
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = event.rawX.toInt()
                    y = event.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val nowX = event.rawX.toInt()
                    val nowY = event.rawY.toInt()
                    val movedX = nowX - x
                    val movedY = nowY - y
                    x = nowX
                    y = nowY
                    layoutParams!!.x = layoutParams!!.x + movedX
                    layoutParams!!.y = layoutParams!!.y + movedY
                    windowManager!!.updateViewLayout(view, layoutParams)
                }
                else -> {
                }
            }
            return false
        }
    }

    companion object {
        var isStarted = false
        var instances: FloatWindowService by Delegates.notNull()
        fun setText(msg: String) = instances.button?.setText(msg)
        fun dismiss() {
            instances.removeView()
        }
        fun show(){
            instances.addView()
        }
    }
}