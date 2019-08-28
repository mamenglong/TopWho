package com.mml.topwho

import android.os.Looper
import android.widget.Toast
import android.provider.Settings.canDrawOverlays
import android.app.AppOpsManager
import android.content.Context
import android.content.Context.APP_OPS_SERVICE
import android.os.Binder
import androidx.core.content.ContextCompat.getSystemService
import android.os.Build
import android.os.Binder.getCallingUid
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.lang.reflect.AccessibleObject.setAccessible


interface UmengEvent{
    companion object{
      const val  OPEN_APPLICATION_LIST="open_application_list"
    }
}