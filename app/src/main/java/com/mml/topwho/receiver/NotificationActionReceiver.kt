package com.mml.topwho.receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.mml.topwho.MainActivity
import com.mml.topwho.R
import com.mml.topwho.service.FloatWindowService
import com.mml.topwho.showToast


class NotificationActionReceiver : BroadcastReceiver() {
    private val TAG = NotificationActionReceiver::class.java.simpleName

    companion object {
        private const val NOTIFICATION_ID = 12
        private const val ACTION_NOTIFICATION_RECEIVER_SHOW =
            "com.mml.topwho.receiver.ACTION_NOTIFICATION_RECEIVER"
        const val ACTION_NOTIFICATION_RECEIVER_RESTORE = "ACTION_NOTIFICATION_RECEIVER_RESTORE"
        const val ACTION_PAUSE = 0
        const val ACTION_RESUME = 1
        const val ACTION_UNLOCK = 2
        const val EXTRA_NOTIFICATION_ACTION = "command"

        fun notification(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //8.0   channelId
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "default"
                val channelName = "通知"
                manager.createNotificationChannel(
                    NotificationChannel(
                        channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                )
            }

            //TaskStackBuilder
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(MainActivity::class.java)
            stackBuilder.addNextIntent(intent)

            val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

            val notification = NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("这是个通知")
                .setContentText("通知")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .build()

            manager.notify(14, notification)
        }

        fun showNotification(context: Context) {
            val pIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                0
            )
            val builder = NotificationCompat
                .Builder(context, "default")
                .setContentTitle(
                    context.getString(
                        R.string.is_running,
                        context.getString(R.string.app_name)
                    )
                )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(context.getString(R.string.touch_to_open))
                .setColor(-0x1dea20)
            builder.addAction(
                R.drawable.ic_noti_action_resume,
                context.getString(R.string.noti_action_resume),
                getPendingIntent(context, ACTION_RESUME)
            )
            builder.addAction(
                R.drawable.ic_noti_action_stop,
                context.getString(R.string.noti_action_stop),
                getPendingIntent(context, ACTION_UNLOCK)
            )
                .setContentIntent(pIntent)

            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "channelId",
                    "通知",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                nm.createNotificationChannel(channel)
            }
            nm.notify(NOTIFICATION_ID, builder.build())

        }

        private fun getPendingIntent(context: Context, command: Int): PendingIntent {
            val intent = Intent(ACTION_NOTIFICATION_RECEIVER_SHOW)
            intent.putExtra(EXTRA_NOTIFICATION_ACTION, command)
            return PendingIntent.getBroadcast(context, command, intent, 0)
        }

        fun cancelNotification(context: Context) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(NOTIFICATION_ID)
        }

        fun register(context: Context, receiver: NotificationActionReceiver) {
            val intentFilter = IntentFilter()
            intentFilter.addAction(ACTION_NOTIFICATION_RECEIVER_SHOW)
            context.registerReceiver(receiver, intentFilter)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive")
        when (intent.getIntExtra(EXTRA_NOTIFICATION_ACTION, -1)) {
            ACTION_RESUME -> {
                showToast("恢复")
                val lollipop = Build.VERSION.SDK_INT < 21
                if (lollipop) {  //在5.0以下通过ActivityManager类的getRunningTasks（）获取当前打开的所有应用程序
                    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    val rtis = am.getRunningTasks(1)
                    val act = (rtis[0].topActivity!!.packageName + "\n"
                            + rtis[0].topActivity!!.className)
                    FloatWindowService.show(act)
                } else {//todo 需要添加》21的逻辑
                    FloatWindowService.show("")
                }
            }
            ACTION_UNLOCK -> {
                showToast("解锁")
                FloatWindowService.unLockView()
                // cancelNotification(context)
            }
        }
    }
}