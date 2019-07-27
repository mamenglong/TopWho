package com.mml.topwho.service

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.annotation.RequiresApi
import com.mml.topwho.TopWhoApplication
import com.mml.topwho.showToast
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
        windowManager!!.removeViewImmediate(button)
        isStarted = false
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        logi("onStartCommand")
        showFloatingWindow()
        isShowed = true
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
        logi("removeView:$button")
        windowManager!!.removeView(button)
    }

    private fun addView() {
        windowManager!!.addView(button, layoutParams)
        logi("addView:$button")
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
        var isShowed = false
        var instances: FloatWindowService by Delegates.notNull()
        fun setText(msg: String): Unit? {
            return instances.button?.setText(msg)
        }
        fun dismiss() {
            Log.i("FloatWindowService", "dismiss before:$isShowed")
            if (isShowed) {
                instances.removeView()
            } else {
                showToast("悬浮窗已经移除")
            }
            isShowed = false
            Log.i("FloatWindowService", "dismiss after:$isShowed")
        }

        fun show(msg: String?=null) {
            Log.i("FloatWindowService", "show")
            if (!isShowed) {
                if (isStarted) {
                    instances.button?.text=msg?:"TopWho Window"
                    
                    if (instances.button != null) {
                        instances.addView()
                    } else {
                        showToast("异常,请重启APP")
                    }
                } else {
                    showToast("请先开启悬浮窗")
                }
            } else {
                showToast("悬浮窗已开启")
            }
            isShowed = true
        }
        fun start(){
            Log.i("FloatWindowService", "start")
            if (!FloatWindowService.isStarted){
                TopWhoApplication.instances?.let{
                    val intent=Intent(it.applicationContext,FloatWindowService::class.java)
                    it.startService(intent)
                    Log.i("FloatWindowService", "started")
                }
            }
        }
        fun stop(){
            Log.i("FloatWindowService", "stop")
            if (FloatWindowService.isStarted){
                FloatWindowService.instances.stopSelf()
                Log.i("FloatWindowService", "stoped")
            }
        }
    }
}