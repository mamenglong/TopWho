package com.mml.topwho.dialog

import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentManager

/**
 * @Author: Menglong Ma
 * @Email: mml2015@126.com
 * @Date: 19-8-29 下午4:19
 * @Description: This is CustomDialogConfig
 * @Package: com.mml.onceapplication.dialog
 * @see [百分比宽高和宽高只有一个生效,如果同时设置则使用宽高,默认为百分比宽高,宽高优先级较高,设置宽高时请使用px像素而不是dp]
 * @Project: OnceApplication
 */
data class CustomDialogConfig(
    /**
     * 是否允许点击窗口外面消失
     */
    var isCancelableOutside: Boolean = true,
    var TAG: String = "CustomDialogConfig",
    val DEFAULT_DIMAMOUNT: Float = 0.2f,
    /**
     * 默认透明度为0.2
     */
    var dimAmount: Float = DEFAULT_DIMAMOUNT,

    /**
     * 获取弹窗显示动画
     */
    var dialogAnimationRes: Int = 0,
    /**
     * 默认弹窗位置为中心
     */
    var gravity: Int = Gravity.CENTER,

    /**
     * 默认宽高为包裹内容
     */
    var width: Int = WindowManager.LayoutParams.WRAP_CONTENT,
    /**
     * 默认宽高为包裹内容
     */
    var height: Int = WindowManager.LayoutParams.WRAP_CONTENT,
    /**
     * 百分比宽
     */
    var percentWidth:Float = 0.8f,
    /**
     * 百分比高
     */
    var percentHeight: Float=0.5f,
    var layoutResId: Int = 0,
    var customDialogView: View? = null,
    var fragmentManager: FragmentManager?=null
)