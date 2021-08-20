package com.mml.topwho.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.UiAutomation
import android.content.Context
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import com.mml.topwho.TopWhoApplication
import java.io.DataOutputStream
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2021/8/20 12:31
 * Description: This is RootUtil
 * Package: com.mml.topwho.util
 * Project: TopWho
 */
object RootUtil {
    fun execWithSu(cmd: String): Boolean {
        return kotlin.runCatching {
            var process: Process? = null
            var os: DataOutputStream? = null
            process = Runtime.getRuntime().exec("su") // 切换到root帐号
            os = DataOutputStream(process.outputStream)
            os.writeBytes(cmd + "\n")
            os.flush()
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor()
            process!!.destroy()
            TimeUnit.SECONDS.sleep(3)
        }.fold(
            onSuccess = {
                Log.i("RootUtil", "execWithSu onSuccess: ---> $it")
                true
            },
            onFailure = {
                Log.i("RootUtil", "execWithSu onFailure: ---> $it")
                false
            }
        )
    }

    fun execAccessibilityCmd(isOpen: Boolean = true): Boolean {
        val pkg = TopWhoApplication.application.packageName
        val accessibleStr = "$pkg/$pkg.service.TopWhoAccessibilityService"
        if (isDeviceRooted()) {
            val str = buildAccessibilityList(accessibleStr, isOpen)
            return execWithSu(str)
        } else {
           return kotlin.runCatching {
                Settings.Secure.putString(
                    TopWhoApplication.application.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, accessibleStr
                )
                Settings.Secure.putString(
                    TopWhoApplication.application.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED, if (isOpen) "1" else "0"
                )//1表示开启
            }.fold(
                onSuccess = {
                    Log.i("RootUtil", "execAccessibilityCmd: --->isOpen:$isOpen result:$it")
                    true
                },
                onFailure = {
                    Log.i("RootUtil", "execAccessibilityCmd: --->isOpen:$isOpen result:$it")
                    false
                }
            )

        }
    }

    //.\adb shell pm grant PKG android.permission.WRITE_SECURE_SETTINGS
    private fun buildAccessibilityList(s: String, isOpen: Boolean): String {
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        Settings.Secure.ACCESSIBILITY_ENABLED
        val p = "settings put secure enabled_accessibility_services "
        return buildString {
            append(p)
            append(s)
            /*   val am = TopWhoApplication.application.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
               am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
                   ?.forEach {
                       append(":${it.id}")
                   }*/
            appendLine()
            if (isOpen) {
                appendLine("settings put secure accessibility_enabled 1")
            } else {
                appendLine("settings put secure accessibility_enabled 0")
            }
        }.also {
            Log.i("RootUtil", "buildAccessibilityList: --->\n$it")
        }

    }

    fun isDeviceRooted(): Boolean {
        val locations = arrayOf(
            "/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
            "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
            "/system/sbin/", "/usr/bin/", "/vendor/bin/"
        )
        for (location in locations) {
            if (File(location + "su").exists()) {
                return true
            }
        }
        return false
    }

}