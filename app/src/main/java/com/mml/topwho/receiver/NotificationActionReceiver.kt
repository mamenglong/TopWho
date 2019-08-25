package com.mml.topwho.receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat.getSystemService
import com.mml.topwho.MainActivity
import com.mml.topwho.R
import com.mml.topwho.service.FloatWindowService
import com.mml.topwho.showToast

class NotificationActionReceiver : BroadcastReceiver() {
    private val TAG = NotificationActionReceiver::class.java.simpleName

    companion object {
        private const val NOTIFICATION_ID = 120
        private const val ACTION_NOTIFICATION_RECEIVER = "com.mml.topwho.receiver.ACTION_NOTIFICATION_RECEIVER"
        const val ACTION_PAUSE = 0
        const val ACTION_RESUME = 1
        const val ACTION_STOP = 2
        const val EXTRA_NOTIFICATION_ACTION = "command"

        fun notification(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

        fun showNotification(context: Context, isPaused: Boolean) {
            val pIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                0
            )
            val builder = NotificationCompat.Builder(context, "default")
                .setContentTitle(
                    context.getString(
                        R.string.is_running,
                        context.getString(R.string.app_name)
                    )
                )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(context.getString(R.string.touch_to_open))
                .setColor(-0x1dea20)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setOngoing(!isPaused)
            if (isPaused) {
                builder.addAction(
                    R.drawable.ic_noti_action_resume, context.getString(R.string.noti_action_resume),
                    getPendingIntent(context, ACTION_RESUME)
                )
            } else {
                builder.addAction(
                    R.drawable.ic_noti_action_pause,
                    context.getString(R.string.noti_action_pause),
                    getPendingIntent(context, ACTION_PAUSE)
                )
            }

            builder.addAction(
                R.drawable.ic_noti_action_stop,
                context.getString(R.string.noti_action_stop),
                getPendingIntent(context, ACTION_STOP)
            )
                .setContentIntent(pIntent)

            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(NOTIFICATION_ID, builder.build())

        }

        private fun getPendingIntent(context: Context, command: Int): PendingIntent {
            val intent = Intent(ACTION_NOTIFICATION_RECEIVER)
            intent.putExtra(EXTRA_NOTIFICATION_ACTION, command)
            return PendingIntent.getBroadcast(context, command, intent, 0)
        }

        fun cancelNotification(context: Context) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(NOTIFICATION_ID)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive")
        when (intent.getIntExtra(EXTRA_NOTIFICATION_ACTION, -1)) {
            ACTION_RESUME -> {
                showToast("恢复")
                showNotification(context, false)
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
            ACTION_PAUSE -> {
                showToast("暂停")
                if (FloatWindowService.isShowed)
                    showNotification(context, true)
            }
            ACTION_STOP -> {
                showToast("停止")
                FloatWindowService.stop()
                cancelNotification(context)
            }
        }
    }

}