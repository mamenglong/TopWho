package com.mml.topwho.data

import android.graphics.drawable.Drawable

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 19-8-2 下午2:46
 * Description: This is AppInfo
 * Package: com.mml.topwho.data
 * Project: TopWho
 */
data class AppInfo(
    var appName: String? = "",
    var packageName: String? = "",
    var versionName: String? = "",
    var className: String? = "",
    var versionCode: Int? = 0,
    var appIcon: Drawable? = null,
    var isSystemApp: Boolean = false,
    var minSdkVersion: Int = 0,
    var taSdkVersion: Int = 0,
    var sourcePath: String? = null,
    var dataDir: String? = null,
    var sourceDir: String? = null
)