package com.mml.topwho.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreferenceCompat
import com.mml.topwho.ConstantString
import com.mml.topwho.R
import com.mml.topwho.sp.SP
import com.mml.topwho.util.Util

/**
 * 项目名称：TopWho
 * Created by Long on 2019/7/23.
 * 修改时间：2019/7/23 22:38
 */
class SwitchFragment : PreferenceFragmentCompat() {

    fun log(msg:String) {
        Log.i(TAG, msg)
    }
    val TAG = "SwitchFragment"
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        preferenceManager.sharedPreferencesName = "setting"
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        preference?.let {
            when (it.key) {
                "switch_open_float_permission" -> {
                    it as SwitchPreferenceCompat
                    Log.i(TAG, "${it.key}:${it.isChecked}")
                    if (it.isChecked) {
                        if (Settings.canDrawOverlays(context)) {
                            if (SP.sp.switch_open_float) {
                                Util.openFloatWindows(true)
                            } else {
                                Util.openFloatWindows(false)
                            }
                        } else {
                            Util.openNoFloatPermissionDialog(this)
                        }
                    }else{
//                        Util.openNoFloatPermissionDialog(this)
                        Util.openFloatWindows(false)
                    }
                }
                "switch_open_float" -> {
                    it as SwitchPreferenceCompat
                    Log.i(TAG, "${it.key}:${it.isChecked}")
                    Util.openFloatWindows(it.isChecked)
                }
                else -> {

                }
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            ConstantString.FloatPermissionRequestCode->{
                if (!Settings.canDrawOverlays(context)) {
                    preferenceScreen.findPreference<SwitchPreferenceCompat>("switch_open_float_permission")?.performClick()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 手动调用
     */
    override fun onResume() {
        super.onResume()
        log("onResume switch_open_float_permission:${preferenceScreen.findPreference<SwitchPreferenceCompat>("switch_open_float_permission")?.isChecked}")
        log("onResume switch_open_float_permission:${ SP.sp.switch_open_float_permission}")
        log("onResume switch_open_float:${preferenceScreen.findPreference<SwitchPreferenceCompat>("switch_open_float")?.isChecked}")
        log("onResume switch_open_float:${SP.sp.switch_open_float}")

        preferenceScreen.findPreference<SwitchPreferenceCompat>("switch_open_float_permission")?.isChecked=SP.sp.switch_open_float_permission
        preferenceScreen.findPreference<SwitchPreferenceCompat>("switch_open_float")?.isChecked=SP.sp.switch_open_float
    }

    override fun onPause() {
        super.onPause()
       log("onPause")
    }

}
