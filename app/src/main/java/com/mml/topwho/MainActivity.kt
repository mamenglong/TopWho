package com.mml.topwho

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.mml.topwho.service.FloatWindowService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        //首次刷新
        refreshSwitch()
        sw_open_float_permission.setOnClickListener { sw ->
            sw as Switch
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    //没有悬浮窗权限
                    AlertDialog.Builder(this)
                        .setMessage(R.string.dialog_enable_overlay_window_msg)
                        .setPositiveButton(
                            R.string.dialog_enable_overlay_window_positive_btn
                        ) { dialog, _ ->
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                            intent.data = Uri.parse("package:$packageName")
                            startActivityForResult(intent, 0)
                            dialog.dismiss()
                        }
                        .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                            refreshSwitch()
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                } else { //有悬浮窗权限
                    if (FloatWindowService.isStarted) {
                        //悬浮窗服务开启
                        if (sw.isChecked) {
                            FloatWindowService.show()
                        } else {
                            FloatWindowService.dismiss()
                        }
                    } else { //没有开启服务去开启
                        startService(Intent(this, FloatWindowService::class.java))
                    }
                }
            }
            refreshSwitch()
        }
        sw_open_accessibility_permission.setOnClickListener { sw ->
            sw as Switch
            if (!isAccessibilityServiceEnabled()) {
                val accessibleIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(accessibleIntent)
            }
        }
    }

    private fun refreshSwitch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) { //没有权限显示开启权限
                sw_open_float_permission.apply {
                    text = getString(R.string.sw_open_float_permission)
                    isChecked = false
                }
            } else {//有权限
                if (sw_open_float_permission.isChecked) { //显示悬浮窗
                    sw_open_float_permission.apply {
                        text = getString(R.string.sw_close_float_window)
                    }
                } else { //隐藏悬浮窗
                    sw_open_float_permission.apply {
                        text = getString(R.string.sw_show_float_window)
                    }
                }

            }
        }
    }

    //检查服务是否开启
    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

        val accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        )
        for (info in accessibilityServices) {
            if (info.id.contains("$packageName/.service.TopWhoAccessibilityService")) {
                return true
            }
        }
        return false
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                sw_open_float_permission.apply {
                    text = getString(R.string.sw_open_float_permission)
                    isChecked = false
                }
                showToast("悬浮窗授权失败!")
            } else {
                sw_open_float_permission.apply {
                    text = getString(R.string.sw_close_float_window)
                    isChecked = true
                }
                showToast("悬浮窗授权成功!")
                startService(Intent(this, FloatWindowService::class.java))
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


}
