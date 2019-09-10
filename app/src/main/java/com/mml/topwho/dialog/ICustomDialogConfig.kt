package com.mml.topwho.dialog

import android.view.View
import androidx.fragment.app.FragmentManager

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 19-9-9 上午11:34
 * Description: This is ICustomDialogConfig
 * Package: com.mml.onceapplication.dialog
 * Project: OnceApplication
 */
interface ICustomDialogConfig {
    fun setLayoutRes(layoutId: Int): CustomDialog

    fun setCustomView(view: View): CustomDialog

    fun setDimAmount(value:Float): CustomDialog
    fun setGravity(value: Int): CustomDialog
    fun setDialogAnimationRes(value: Int): CustomDialog
    fun setWidth(value: Int): CustomDialog
    fun setPercentWidth(float: Float):CustomDialog
    fun setHeight(value: Int): CustomDialog
    fun setPercentHeight(value: Float): CustomDialog
    fun setTag(value: String): CustomDialog
    fun setCancelableOutside(value: Boolean): CustomDialog
    fun setFragmentManager(fragmentManager: FragmentManager) :CustomDialog
    fun setDialogConfig(config: CustomDialogConfig):CustomDialog
}