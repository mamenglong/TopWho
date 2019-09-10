package com.mml.topwho.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 19-8-29 下午12:00
 * Description:[DialogFragment的基类1.系统默认onCreateDialog方法返回一个Dialog对象,对其不做处理2.主要操作onCreateView方法 因为DialogFragment继承自Fragment,所以可以在onCreteView()方法返回xml布局,该布局在onActivityCreated()方法中,设置给系统之前创建的Dialog对象]
 * Package: com.mml.onceapplication.dialog
 * Project: OnceApplication
 */
abstract class BaseCustomDialog : DialogFragment() {
   protected val dialogConfig: CustomDialogConfig = CustomDialogConfig()

    protected abstract fun bindView(view: View)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View? = null
        if (dialogConfig.layoutResId > 0) {
            view = inflater.inflate(dialogConfig.layoutResId, container, false)
        }
        dialogConfig.customDialogView?.let { view=it }
       // log(dialogConfig.width.toString()+" "+view?.measuredWidth+" "+ view?.layoutParams?.width,tag="BaseCustomDialog")
        view?.apply { bindView(this)}?:throw NullPointerException("Root View is null.property layoutResId:(Int) or customDialogView:(View) should be initialized.")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //去除Dialog默认头部
        val dialog = dialog
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(dialogConfig.isCancelableOutside)
        if (dialog.window != null && dialogConfig.dialogAnimationRes > 0) {
            dialog.window!!.setWindowAnimations(dialogConfig.dialogAnimationRes)
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window
        if (window != null) {
            //设置窗体背景色透明
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //设置宽高
            val layoutParams = window.attributes

            layoutParams.width =if (dialogConfig.width!=WindowManager.LayoutParams.WRAP_CONTENT) dialogConfig.width  else (getScreenWidth(context!!)*dialogConfig.percentWidth).toInt()
            layoutParams.height = if (dialogConfig.height!= WindowManager.LayoutParams.WRAP_CONTENT) dialogConfig.height else (getScreenHeight(context!!)*dialogConfig.percentHeight).toInt()
            //透明度
            layoutParams.dimAmount = dialogConfig.dimAmount
            //位置
            layoutParams.gravity = dialogConfig.gravity
            window.attributes = layoutParams
        }
    }

    fun show(fragmentManager: FragmentManager) {

        show(fragmentManager, dialogConfig.TAG)
    }
    //获取设备屏幕宽度
    private fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    //获取设备屏幕高度
    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }
}