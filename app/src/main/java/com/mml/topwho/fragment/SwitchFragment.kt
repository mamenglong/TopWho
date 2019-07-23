package com.mml.topwho.fragment

import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.mml.topwho.R
import com.mml.topwho.checkFloatPermission

/**
 * 项目名称：TopWho
 * Created by Long on 2019/7/23.
 * 修改时间：2019/7/23 22:38
 */
class SwitchFragment : PreferenceFragmentCompat() {
    val TAG="SwitchFragment"
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "setting"
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        preference?.let {
            when(it.key) {
                "switch_open_float_permission"->{
                    it as SwitchPreferenceCompat
                    Log.i(TAG,"${it.key}:${it.isChecked}")
                    checkFloatPermission(context!!)
                }
                "switch_open_float" ->{
                    it as SwitchPreferenceCompat
                    Log.i(TAG,"${it.key}:${it.isChecked}")
                }
                else->{

                }
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

}
