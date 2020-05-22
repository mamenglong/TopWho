package com.mml.topwho

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.mml.topwho.fragment.SwitchFragment
import com.mml.topwho.service.FloatWindowService
import com.mml.topwho.service.TopWhoAccessibilityService
import com.mml.topwho.sp.SP
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_main.*
import tyrantgit.explosionfield.ExplosionField
import kotlin.jvm.internal.Intrinsics


class MainActivity : AppCompatActivity() {
    val sp by lazy { SP(this) }
    val TAG = "MainActivity"
    lateinit var mExplosionField: ExplosionField
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        // NotificationActionReceiver.showNotification(this, false)
        mExplosionField = ExplosionField.attach2Window(this)
        //NotificationActionReceiver.notification(this)
    }

    private fun initView() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, SwitchFragment())
            .commit()
        sw_open_accessibility_permission.setOnClickListener { sw ->
            sw as Switch
            if (!isAccessibilityServiceEnabled()) {
                val accessibleIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                val flattenToString: String =
                    ComponentName(
                        this.packageName,
                        TopWhoAccessibilityService::class.java.name
                    ).flattenToString()
                Intrinsics.checkExpressionValueIsNotNull(
                    flattenToString,
                    "ComponentName(App.INS.pa…s.name).flattenToString()"
                )
                val bundle = Bundle()
                bundle.putString(":settings:fragment_args_key", flattenToString)
                accessibleIntent.putExtra(":settings:fragment_args_key", flattenToString)
                accessibleIntent.putExtra(":settings:show_fragment_args", bundle)
                startActivity(accessibleIntent)
            }
        }
        btn_open_float.setOnClickListener {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
        btn_open_accessibility.setOnClickListener {
            val accessibleIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            accessibleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)//268435456
            val flattenToString: String =
                ComponentName(
                    this.packageName,
                    TopWhoAccessibilityService::class.java.name
                ).flattenToString()
            Intrinsics.checkExpressionValueIsNotNull(
                flattenToString,
                "ComponentName(App.INS.pa…s.name).flattenToString()"
            )
            val bundle = Bundle()
            bundle.putString(":settings:fragment_args_key", flattenToString)
            accessibleIntent.putExtra(":settings:fragment_args_key", flattenToString)
            accessibleIntent.putExtra(":settings:show_fragment_args", bundle)
            startActivity(accessibleIntent)
        }
        app_list.setOnClickListener {
//            mExplosionField.explode(it)
            /* val map = HashMap<String, String>(1)
             map[UmengEvent.OPEN_APPLICATION_LIST] = ""*/
            MobclickAgent.onEvent(this, UmengEvent.OPEN_APPLICATION_LIST)
            startActivity(Intent(this, AppListActivity::class.java))
        }

    }


    private fun initSwitchStatus() {
        Log.i("MainActivity", "initSwitchStatus")
        sw_open_accessibility_permission.isChecked = isAccessibilityServiceEnabled()
        SP.sp.switch_open_float_permission = Settings.canDrawOverlays(this)
        if (isAccessibilityServiceEnabled()) {
            btn_open_accessibility.setDrawableRight(R.drawable.ic_check_circle_48dp)
        } else {
            btn_open_accessibility.setDrawableRight(R.drawable.ic_uncheck_circle_48dp)
        }
        if (Settings.canDrawOverlays(this)) {
            btn_open_float.setDrawableRight(R.drawable.ic_check_circle_48dp)
        } else {
            btn_open_float.setDrawableRight(R.drawable.ic_uncheck_circle_48dp)
        }
    }

    //检查服务是否开启
    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityManager =
            getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

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


    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
        Log.i("MainActivity", "onResume")
        initSwitchStatus()
        run { supportFragmentManager.fragments[0] as SwitchFragment? }?.let {
            it.updateSwitchStatus()
        }
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    fun checkFloatPermission(context: Context): Boolean {
        Log.i(TAG, "checkFloatPermission")
        if (!Settings.canDrawOverlays(context)) {
            //没有悬浮窗权限
            AlertDialog.Builder(context)
                .setMessage(R.string.dialog_enable_overlay_window_msg)
                .setPositiveButton(
                    R.string.dialog_enable_overlay_window_positive_btn
                ) { dialog, _ ->
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    intent.data = Uri.parse("package:${context.packageName}")
                    startActivityForResult(intent, 0)
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
            return false
        } else { //有悬浮窗权限
            openFloatWindow()
            return true
        }

    }

    private fun openFloatWindow() {
        if (FloatWindowService.isStarted) {
            //悬浮窗服务开启
            if (sp.switch_open_float) {
                FloatWindowService.show("")
            } else {
                FloatWindowService.dismiss()
            }
        } else { //没有开启服务去开启
            startService(Intent(this, FloatWindowService::class.java))
            FloatWindowService.show("")
        }
    }
}
