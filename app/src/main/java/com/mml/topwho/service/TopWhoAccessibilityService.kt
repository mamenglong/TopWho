package com.mml.topwho.service

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import kotlin.properties.Delegates

class TopWhoAccessibilityService : AccessibilityService() {

    fun logi(msg: String){
        Log.i(TAG,msg)
    }
    companion object{
        var instances: TopWhoAccessibilityService by Delegates.notNull()
    }
    private val TAG= TopWhoAccessibilityService::class.java.simpleName
    override fun onInterrupt() {
        Log.i(TAG,"onInterrupt")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        Log.i(TAG,"onAccessibilityEvent")
        when (p0?.eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                Log.i(TAG,"TYPE_VIEW_CLICKED")
            }
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val result= "${p0.packageName} \n ${p0.className} "
                FloatWindowService.setText(result)
                logi("${p0.packageName} \n ${p0.className} ")
                Log.i(TAG,result)
            }
            null -> {

            }
        }

    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG,"onUnbind")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG,"onStartCommand")

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG,"onServiceConnected")
        instances =this

    }
}