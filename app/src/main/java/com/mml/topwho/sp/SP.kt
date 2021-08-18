package com.mml.topwho.sp

import android.content.Context
import com.mml.topwho.TopWhoApplication

class SP(context: Context):SharedPreferencesUtils(context) {
    var switch_open_float_permission by SharedPreferenceDelegates.boolean()
    var switch_open_float by SharedPreferenceDelegates.boolean()
    companion object{
        val sp by lazy { SP(TopWhoApplication.application!!.applicationContext) }
    }
}