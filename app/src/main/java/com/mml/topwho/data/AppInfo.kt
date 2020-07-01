package com.mml.topwho.data

import android.graphics.drawable.Drawable
import androidx.annotation.Keep
import com.mml.topwho.annotatio.FieldOrderAnnotation
import com.mml.topwho.py.PY

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 19-8-2 下午2:46
 * Description: This is AppInfo
 * Package: com.mml.topwho.data
 * Project: TopWho
 */
@Keep
data class AppInfo(
    @FieldOrderAnnotation(1)
    var appName: String? = "",
    @FieldOrderAnnotation(2)
    var packageName: String? = "",
    @FieldOrderAnnotation(4)
    var versionName: String? = "",
    @FieldOrderAnnotation(3)
    var className: String? = "",
    @FieldOrderAnnotation(5)
    var versionCode: Long = 0,
    @FieldOrderAnnotation(6)
    var appIcon: Drawable? = null,
    @FieldOrderAnnotation(7)
    var isSystemApp: Boolean = false,
    @FieldOrderAnnotation(8)
    var minSdkVersion: Int = 0,
    @FieldOrderAnnotation(9)
    var taSdkVersion: Int = 0,
    @FieldOrderAnnotation(10)
    var sourcePath: String? = null,
    @FieldOrderAnnotation(11)
    var dataDir: String? = null,
    @FieldOrderAnnotation(12)
    var sourceDir: String? = null,
    @FieldOrderAnnotation(13)
    override var firstChar: Char = '#',
    @FieldOrderAnnotation(14)
    override var firstChars: String = "",
    @FieldOrderAnnotation(15)
    override var pinyins: String = "",
    @FieldOrderAnnotation(16)
    override var pinyinsTotalLength: Int = 0
) : PY {
    override fun zh(): String {
        return appName ?: "名字丢了"
    }
}