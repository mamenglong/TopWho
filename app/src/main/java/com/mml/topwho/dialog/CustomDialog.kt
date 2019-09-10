package com.mml.topwho.dialog

import android.content.DialogInterface
import android.view.View
import androidx.fragment.app.FragmentManager


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 19-8-29 下午12:00
 * Description:[DialogFragment的基类1.系统默认onCreateDialog方法返回一个Dialog对象,对其不做处理2.主要操作onCreateView方法 因为DialogFragment继承自Fragment,所以可以在onCreteView()方法返回xml布局,该布局在onActivityCreated()方法中,设置给系统之前创建的Dialog对象]
 * Package: com.mml.onceapplication.dialog
 * Project: OnceApplication
 */


class CustomDialog : BaseCustomDialog(),ICustomDialogConfig {
/*    override fun show() {
        dialogConfig.fragmentManager?.apply {
            show(this)
        }?:throw NullPointerException("Parameter fragmentManager is null ,please call setFragmentManager(fragmentManager) before show()")
    }*/

    private var onDismissCallback: (() -> Unit)? = null
    private var convert: ((view: View) -> Unit)? = null

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissCallback?.invoke()
    }


     fun setOnDismissCallback(onDismissCallback: () -> Unit): CustomDialog {
        this.onDismissCallback = onDismissCallback
        return this
    }

    fun convert(convert: (view: View) -> Unit): CustomDialog {
        this.convert = convert
        return this
    }

    override fun bindView(view: View) {
        convert?.invoke(view)
    }

    override fun setLayoutRes(layoutId: Int): CustomDialog {
        dialogConfig.layoutResId = layoutId
        return this
    }

    override fun setCustomView(view: View): CustomDialog {
        dialogConfig.customDialogView = view
        return this
    }
    override fun setDimAmount(value:Float): CustomDialog {
        dialogConfig.dimAmount = value
        return this
    }
    override fun setGravity(value: Int): CustomDialog {
        dialogConfig.gravity=value
        return this
    }
    override fun setDialogAnimationRes(value: Int): CustomDialog {
        dialogConfig.dialogAnimationRes=value
        return this
    }
    override fun setWidth(value: Int): CustomDialog {
        dialogConfig.width=value
        return this
    }
    override fun setPercentWidth(float: Float): CustomDialog {
        dialogConfig.percentWidth=float
        return  this
    }
    override fun setHeight(value: Int): CustomDialog {
        dialogConfig.height=value
        return this
    }
    override fun setPercentHeight(value: Float): CustomDialog {
        dialogConfig.percentHeight=value
        return this
    }
    override fun setTag(value: String): CustomDialog {
        dialogConfig.TAG=value
        return this
    }
    override fun setCancelableOutside(value: Boolean): CustomDialog {
        dialogConfig.isCancelableOutside=value
        return this
    }
    override fun setFragmentManager(fragmentManager: FragmentManager): CustomDialog {
        dialogConfig.fragmentManager=fragmentManager
        return this
    }
    override fun setDialogConfig(config: CustomDialogConfig): CustomDialog {
        dialogConfig.let {
            with(config){
                it.TAG=TAG
                it.isCancelableOutside=isCancelableOutside
                it.percentHeight=percentHeight
                it.percentWidth=percentWidth
                it.width=width
                it.height=height
                it.gravity=gravity
                it.layoutResId=layoutResId
                it.customDialogView=customDialogView
                it.dialogAnimationRes=dialogAnimationRes
                it.dimAmount=dimAmount
            }
        }
        return  this
    }
   /* fun setLayoutRes(layoutId: Int): CustomDialog {
        dialogConfig.layoutResId = layoutId
        return this
    }

    fun setCustomView(view: View): CustomDialog {
        dialogConfig.customDialogView = view
        return this
    }

    fun setOnDismissCallback(onDismissCallback: () -> Unit): CustomDialog {
        this.onDismissCallback = onDismissCallback
        return this
    }

    fun convert(convert: (view: View) -> Unit): CustomDialog {
        this.convert = convert
        return this
    }
    fun setDimAmount(value:Float): CustomDialog {
        dialogConfig.dimAmount = value
        return this
    }
    fun setGravity(value: Int): CustomDialog {
        dialogConfig.gravity=value
        return this
    }
    fun setDialogAnimationRes(value: Int): CustomDialog {
        dialogConfig.dialogAnimationRes=value
        return this
    }
    fun setWidth(value: Int): CustomDialog {
        dialogConfig.width=value
        return this
    }
    fun setPercentWidth(float: Float):CustomDialog{
        dialogConfig.percentWidth=float
        return  this
    }
    fun setHeight(value: Int): CustomDialog {
        dialogConfig.height=value
        return this
    }
    fun setPercentHeight(value: Float): CustomDialog {
        dialogConfig.percentHeight=value
        return this
    }
    fun setTag(value: String): CustomDialog {
        dialogConfig.TAG=value
        return this
    }
    fun setCancelableOutside(value: Boolean): CustomDialog {
        dialogConfig.isCancelableOutside=value
        return this
    }
    fun setFragmentManager(fragmentManager: FragmentManager){
        dialogConfig.fragmentManager=fragmentManager
    }
    fun setDialogConfig(config: CustomDialogConfig):CustomDialog{
        dialogConfig.let {
            with(config){
                it.TAG=TAG
                it.isCancelableOutside=isCancelableOutside
                it.percentHeight=percentHeight
                it.percentWidth=percentWidth
                it.width=width
                it.height=height
                it.gravity=gravity
                it.layoutResId=layoutResId
                it.customDialogView=customDialogView
                it.dialogAnimationRes=dialogAnimationRes
                it.dimAmount=dimAmount
            }
        }
        return  this
    }*/
}