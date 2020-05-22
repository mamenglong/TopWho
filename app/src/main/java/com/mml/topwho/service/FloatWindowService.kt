package com.mml.topwho.service

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.mml.topwho.R
import com.mml.topwho.TopWhoApplication
import com.mml.topwho.TouchContainerLayout
import com.mml.topwho.receiver.NotificationActionReceiver
import com.mml.topwho.showToast
import kotlinx.android.synthetic.main.float_view.view.*
import kotlin.properties.Delegates


class FloatWindowService : Service() {
    fun logi(msg: String) {
        Log.i(FloatWindowService::class.java.simpleName, msg)
    }

    private var notificationReceiver: NotificationActionReceiver = NotificationActionReceiver()
    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null

    private var container: ViewGroup? = null

    override fun onCreate() {
        logi("onCreate")
        super.onCreate()
        NotificationActionReceiver.register(this, notificationReceiver)
        NotificationActionReceiver.showNotification(this)
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
        layoutParams!!.height = 300
        layoutParams!!.x = 300
        layoutParams!!.y = 300
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        logi("onDestroy")
        try {
            windowManager!!.removeViewImmediate(container)
        } catch (e: Exception) {
        }
        isStarted = false
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        logi("onStartCommand")
        initFloatingWindow()
        isShowed = true
        instances = this
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initFloatingWindow() {
        container = TouchContainerLayout(applicationContext)
        val view =
            LayoutInflater.from(applicationContext).inflate(R.layout.float_view, container!!, false)
        container!!.apply {
            this as TouchContainerLayout
            addView(view)
            view.tv_text.text = "TopWho Window"
            setBackgroundColor(Color.argb(128, 255, 255, 255))
            view.tv_text.textSize = 10f
            view.tv_text.isAllCaps = false
            FloatingListener().let {
                setOnTouchListener(it)
            }
        }
        container?.tv_text?.setOnClickListener {
            it as TextView
            //获取剪贴板管理器：
            val cm: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData = ClipData.newPlainText("Label", "${it.text}")
            cm.setPrimaryClip(mClipData)
            showToast("复制成功")
        }
        container?.iv_lock?.setOnClickListener {
            it as ImageView
            it.setImageResource(R.drawable.ic_lock_outline_black_24dp)
            layoutParams!!.flags
            layoutParams!!.flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            windowManager!!.updateViewLayout(container, layoutParams)
        }
        container?.iv_visibility?.setOnClickListener {
            dismiss()
        }
        addView()
    }

    fun unlockView() {
        container?.iv_lock?.setImageResource(R.drawable.ic_lock_open_black_24dp)
        layoutParams!!.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        windowManager!!.updateViewLayout(container, layoutParams)
    }

    private fun removeView() {
        logi("removeView:$container")
        windowManager!!.removeView(container)
    }

    private fun addView() {
        windowManager!!.addView(container, layoutParams)
        logi("addView:$container")
    }

    private inner class FloatingListener : View.OnTouchListener {//, View.OnClickListener {
/*        override fun onClick(p0: View?) {

            logi("onClick")
//            val accessibleIntent =  Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
//            startActivity(accessibleIntent)
        }*/

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
            return instances.container?.tv_text?.setText(msg)
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

        fun show(msg: String? = null) {
            Log.i("FloatWindowService", "show")
            if (!isShowed) {
                if (isStarted) {
                    instances.container?.tv_text?.text = msg ?: "TopWho Window"

                    if (instances.container != null) {
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

        fun start() {
            Log.i("FloatWindowService", "start")
            if (!FloatWindowService.isStarted) {
                TopWhoApplication.instances?.let {
                    val intent = Intent(it.applicationContext, FloatWindowService::class.java)
                    it.startService(intent)
                    Log.i("FloatWindowService", "started")
                }
            }
        }

        fun stop() {
            Log.i("FloatWindowService", "stop")
            if (FloatWindowService.isStarted) {
                FloatWindowService.instances.stopSelf()
                Log.i("FloatWindowService", "stoped")
            }
        }

        fun unLockView() {
            if (isShowed) {
                instances.unlockView()
            }
        }
    }
}