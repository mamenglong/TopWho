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
import java.lang.reflect.AccessibleObject.setAccessible




fun showToast(msg:String)= run {
    val looper=Looper.getMainLooper()
    if (looper== Looper.myLooper()){
            Toast.makeText(TopWhoApplication.instances,msg,Toast.LENGTH_SHORT).show()
    }
}
fun checkFloatPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
        return true
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        try {
            var cls = Class.forName("android.content.Context")
            val declaredField = cls.getDeclaredField("APP_OPS_SERVICE")
            declaredField.isAccessible = true
            var obj: Any? = declaredField.get(cls) as? String ?: return false
            obj = cls.getMethod("getSystemService", String::class.java).invoke(context, obj)
            cls = Class.forName("android.app.AppOpsManager")
            val declaredField2 = cls.getDeclaredField("MODE_ALLOWED")
            declaredField2.isAccessible = true
            val checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String::class.java)
            val result = checkOp.invoke(obj, 24, Binder.getCallingUid(), context.packageName) as Int
            return result == declaredField2.getInt(cls)
        } catch (e: Exception) {
            return false
        }

    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appOpsMgr = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOpsMgr.checkOpNoThrow(
                "android:system_alert_window", android.os.Process.myUid(), context
                    .packageName
            )
            return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED
        } else {
            return Settings.canDrawOverlays(context)
        }
    }
}