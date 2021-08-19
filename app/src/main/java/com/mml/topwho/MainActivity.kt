package com.mml.topwho

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.Switch
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.mml.topwho.databinding.ActivityMainBinding
import com.mml.topwho.fragment.SwitchFragment
import com.mml.topwho.receiver.NotificationActionReceiver
import com.mml.topwho.service.FloatWindowService
import com.mml.topwho.service.TopWhoAccessibilityService
import com.mml.topwho.sp.SP
import com.umeng.analytics.MobclickAgent
import tyrantgit.explosionfield.ExplosionField
import java.io.DataOutputStream
import java.lang.Exception
import java.security.Permission
import kotlin.jvm.internal.Intrinsics
import androidx.core.app.ActivityCompat.startActivityForResult


class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    val sp by lazy { SP(this) }
    val TAG = "MainActivity"
    lateinit var mExplosionField: ExplosionField
    private val writeSettingActivityResultContracts =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Settings.System.canWrite(this)) {
                writeSetting()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        initView()
        //NotificationActionReceiver.showNotification(this)
        mExplosionField = ExplosionField.attach2Window(this)
        // NotificationActionReceiver.notification(this)
    }

    private fun initView() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, SwitchFragment())
            .commit()
        activityMainBinding.swOpenAccessibilityPermission.setOnClickListener { sw ->
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
        activityMainBinding.btnOpenFloat.setOnClickListener {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
        activityMainBinding.btnOpenAccessibility.setOnClickListener {
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
        activityMainBinding.appList.setOnClickListener {
//            mExplosionField.explode(it)
            /* val map = HashMap<String, String>(1)
             map[UmengEvent.OPEN_APPLICATION_LIST] = ""*/
            MobclickAgent.onEvent(this, UmengEvent.OPEN_APPLICATION_LIST)
            startActivity(Intent(this, AppListActivity::class.java))
        }

        activityMainBinding.btnRoot.setOnClickListener {
            kotlin.runCatching {
                upgradeRootPermission(packageCodePath)
            }.onSuccess {
                showToast("root权限:${it}")
                if (it) {
                    if (Settings.System.canWrite(this)) {
                        writeSetting()
                    } else {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_WRITE_SETTINGS,
                            Uri.parse("package:$packageName")
                        )
                        writeSettingActivityResultContracts.launch(intent)
                    }
                }
            }.onFailure {
                showToast("${it.message}")
            }
        }
    }

    private fun writeSetting() {
        Settings.Secure.putString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
            "com.mml.topwho/com.mml.topwho.service.TopWhoAccessibilityService"
        );
        Settings.Secure.putString(
            contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED, "1"
        )
        initSwitchStatus()
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    private fun upgradeRootPermission(pkgCodePath: String): Boolean {
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            val cmd = "chmod 777 $pkgCodePath"
            process = Runtime.getRuntime().exec("su") // 切换到root帐号
            os = DataOutputStream(process.outputStream)
            os.use {
                os.writeBytes(cmd)
                os.writeBytes("exit\n")
                os.flush()
            }
            process.waitFor()
            process!!.destroy()
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun initSwitchStatus() {
        Log.i("MainActivity", "initSwitchStatus")
        activityMainBinding.swOpenAccessibilityPermission.isChecked =
            isAccessibilityServiceEnabled()
        SP.sp.switch_open_float_permission = Settings.canDrawOverlays(this)
        if (isAccessibilityServiceEnabled()) {
            activityMainBinding.btnOpenAccessibility.setDrawableRight(R.drawable.ic_check_circle_48dp)
        } else {
            activityMainBinding.btnOpenAccessibility.setDrawableRight(R.drawable.ic_uncheck_circle_48dp)
        }
        if (Settings.canDrawOverlays(this)) {
            activityMainBinding.btnOpenFloat.setDrawableRight(R.drawable.ic_check_circle_48dp)
        } else {
            activityMainBinding.btnOpenFloat.setDrawableRight(R.drawable.ic_uncheck_circle_48dp)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, FloatWindowService::class.java))
            } else {
                startService(Intent(this, FloatWindowService::class.java))
            }
            FloatWindowService.show("")
        }
    }
}
