package com.mml.topwho.service

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.mml.topwho.*
import com.mml.topwho.databinding.FloatViewBinding
import com.mml.topwho.receiver.NotificationActionReceiver
import kotlin.math.abs
import kotlin.properties.Delegates


class FloatWindowService : Service() {
    fun logi(msg: String) {
        Log.i(FloatWindowService::class.java.simpleName, msg)
    }

    private var notificationReceiver: NotificationActionReceiver = NotificationActionReceiver()
    private lateinit var windowManager: WindowManager
    private lateinit var layoutParams: WindowManager.LayoutParams

    private lateinit var container: ViewGroup
    private lateinit var binding: FloatViewBinding
    override fun onCreate() {
        logi("onCreate")
        super.onCreate()
        NotificationActionReceiver.register(this, notificationReceiver)
        NotificationActionReceiver.showNotification(this)
        isStarted = true
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layoutParams.format = PixelFormat.RGBA_8888
        layoutParams.gravity = Gravity.LEFT or Gravity.TOP
        layoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.width = 500
        layoutParams.height = 300
        layoutParams.x = 300
        layoutParams.y = 300
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        logi("onDestroy")
        try {
            windowManager.removeViewImmediate(container)
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
        binding = FloatViewBinding.inflate(LayoutInflater.from(applicationContext))
        container = binding.root
        FloatingListener().let {
            binding.root.setOnTouchListener(it)
        }
        binding.root.setOnClickListener {
            if (layoutParams.height == 300) {
                layoutParams.height = 150
                binding.ivLock.gone()
                binding.ivVisibility.gone()
            } else {
                layoutParams.height = 300
                binding.ivLock.visible()
                binding.ivVisibility.visible()
            }
            updateView()
        }
        binding.tvText.text = "TopWho Window"
        binding.tvText.textSize = 10f
        binding.tvText.isAllCaps = false
        binding.tvText.setOnClickListener {
            it as TextView
            //获取剪贴板管理器：
            val cm: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData = ClipData.newPlainText("Label", "${it.text}")
            cm.setPrimaryClip(mClipData)
            showToast("复制成功")
        }
        binding.ivLock.setOnClickListener {
            it as ImageView
            it.setImageResource(R.drawable.ic_lock_outline_black_24dp)
            layoutParams.flags
            layoutParams.flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            updateView()
            //NotificationActionReceiver.showNotification(this)
        }
        binding.ivVisibility.setOnClickListener {
            dismiss()
            //NotificationActionReceiver.showNotification(this)
        }
        addView()
    }

    fun setText(msg: String) {
        binding.tvText.text = msg
        val width = maxOf(binding.tvText.measuredWidth, 500)
        layoutParams.width = width
        updateView()
    }

    fun unlockView() {
        binding.ivLock.setImageResource(R.drawable.ic_lock_open_black_24dp)
        layoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        updateView()
    }

    private fun removeView() {
        logi("removeView:$container")
        windowManager.removeView(container)
    }
    private fun updateView() {
        windowManager.updateViewLayout(container, layoutParams)
        logi("updateView:$container")
    }
    private fun addView() {
        windowManager.addView(container, layoutParams)
        logi("addView:$container")
    }

    private inner class FloatingListener : View.OnTouchListener {
        private var lastX = 0f
        private var lastY = 0f
        private var mLastDownTime = 0L
        private val mTouchSlop =
            ViewConfiguration.get(TopWhoApplication.application).scaledTouchSlop
        private var canMoveFlag = false
        private val countDownRunnable = Runnable {
            canMoveFlag = true
        }
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            val x: Float = event.rawX
            val y: Float = event.rawY
            val action = event.action and MotionEvent.ACTION_MASK
            logi("onTouch:$action")
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = x
                    lastY = y
                    mLastDownTime = System.currentTimeMillis()
                    view.postDelayed(countDownRunnable,200)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (canMoveFlag) {
                        val movedX = (x - lastX).toInt()
                        val movedY = (y - lastY).toInt()
                        lastX = x
                        lastY = y
                        layoutParams.x = layoutParams.x + movedX
                        layoutParams.y = layoutParams.y + movedY
                        windowManager.updateViewLayout(view, layoutParams)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    view.removeCallbacks(countDownRunnable)
                    val isClick = isClick(event)
                    logi("isClick:$isClick")
                    if (isClick) {
                        view.performClick()
                    }
                }
                else -> {

                }
            }
            return true
        }

        private fun isClick(event: MotionEvent): Boolean {
            val offsetX = abs(event.rawX - lastX)
            val offsetY = abs(event.rawY - lastY)
            val time = System.currentTimeMillis() - mLastDownTime
            logi("isClick:$offsetX $offsetY $time $mTouchSlop")
            return offsetX < mTouchSlop * 2f && offsetY < mTouchSlop * 2f && time < 300L
        }
    }

    companion object {
        var isStarted = false
        var isShowed = false
        var instances: FloatWindowService by Delegates.notNull()
        fun setText(msg: String) {
            instances.setText(msg)
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
                    instances.addView()
                    instances.setText(
                        if (msg.isNullOrBlank()) "TopWho Window" else msg
                    )
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
            if (!isStarted) {
                val intent = Intent(
                    TopWhoApplication.application.applicationContext,
                    FloatWindowService::class.java
                )
                TopWhoApplication.application.startService(intent)
                Log.i("FloatWindowService", "started")
            }
        }

        fun stop() {
            Log.i("FloatWindowService", "stop")
            if (isStarted) {
                instances.stopSelf()
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