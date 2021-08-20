package com.mml.topwho.util

import android.app.AppOpsManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.mml.topwho.ConstantString
import com.mml.topwho.R
import com.mml.topwho.TopWhoApplication
import com.mml.topwho.fragment.SwitchFragment
import com.mml.topwho.service.FloatWindowService
import com.mml.topwho.showToast
import com.mml.topwho.sp.SP

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 19-7-26 上午11:28
 * Description: This is Util
 * Package: com.mml.topwho.util
 * Project: TopWho
 */
class Util {
      companion object{
          /**
           * Author: Menglong Ma
           * Email: mml2015@126.com
           * Date: 19-7-26 上午11:57
           * Description: This is 检查悬浮窗权限
           * @params []
           * @return   Boolean
          */
          fun checkFloatPermission(context: Context): Boolean {
              if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                  return true
              if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                  try {
                      var cls = Class.forName("android.content.Context")
                      val declaredField = cls.getDeclaredField("APP_OPS_SERVICE")
                      declaredField.isAccessible = true
                      var obj: Any? = declaredField.get(cls) as? String ?: return false
                      obj = cls.getMethod("getSystemService", String::class.java).invoke(context, obj)
                      cls = Class.forName("android.app.AppOpsManager")
                      val declaredField2 = cls.getDeclaredField("MODE_ALLOWED")
                      declaredField2.isAccessible = true
                      val checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String::class.java)
                      val result = checkOp.invoke(obj, 24, Binder.getCallingUid(), context.packageName) as Int
                      return result == declaredField2.getInt(cls)
                  } catch (e: Exception) {
                      return false
                  }

              } else {
                  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                      val appOpsMgr = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                      val mode = appOpsMgr.checkOpNoThrow(
                          "android:system_alert_window", android.os.Process.myUid(), context
                              .packageName
                      )
                      mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED
                  } else {
                      Settings.canDrawOverlays(context)
                  }
              }
          }

          /**
           * Author: Menglong Ma
           * Email: mml2015@126.com
           * Date: 19-7-26 下午12:01
           * Description: This is 没有权限时打开弹窗,去开启
           * @params []
           * @return
          */
          fun openNoFloatPermissionDialog(fragment: Fragment){
              //没有悬浮窗权限
              fragment as SwitchFragment
              AlertDialog.Builder(fragment.context!!)
                  .setMessage(R.string.dialog_enable_overlay_window_msg)
                  .setPositiveButton(
                      R.string.dialog_enable_overlay_window_positive_btn
                  ) { dialog, _ ->
                      val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                      intent.data = Uri.parse("package:${fragment.context!!.packageName}")
                      fragment.startActivityForResult(intent,ConstantString.FloatPermissionRequestCode)
                      dialog.dismiss()
                  }
                  .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                      SP.sp.switch_open_float_permission=false
                      fragment.updateSwitchStatus()
                      dialog.dismiss()
                  }
                  .setCancelable(false)
                  .create()
                  .show()
          }

          /**
           * Author: Menglong Ma
           * Email: mml2015@126.com
           * Date: 19-7-26 下午2:01
           * Description: This is  开关弹窗
           * @params []
           * @return
          */
          fun openFloatWindows(open:Boolean){
              if (open){
                  if (FloatWindowService.isStarted){
                      if (FloatWindowService.isShowed) {
                          showToast("弹窗已开启")
                      }else{
                          FloatWindowService.show()
                      }
                  } else{
                      TopWhoApplication.application.let {
                          it.startService(Intent(it,FloatWindowService::class.java))
                      }
                  }
              } else{
                  if (FloatWindowService.isStarted){
                      if(FloatWindowService.isShowed){
                          FloatWindowService.dismiss()
                      }  else{
                          showToast("弹窗已关闭")
                      }
                  }else{
                      showToast("服务未开启,弹窗已关闭")
                  }
              }
          }
          fun getAppName( context:Context,packageName:String): String {
             val packageManager = context.packageManager
              val packageInfo = packageManager.getPackageInfo(
                  packageName, 0)
              return try {
                  packageInfo.applicationInfo.loadLabel(packageManager).toString()
              } catch (e: Exception) {
                  ""
              }
          }

          fun copyText(context: Context,msg:String){
              //获取剪贴板管理器：
              val cm: ClipboardManager =
                  context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val mClipData = ClipData.newPlainText("Label", msg)
              cm.setPrimaryClip(mClipData)
              showToast("复制成功")
          }
      }
}