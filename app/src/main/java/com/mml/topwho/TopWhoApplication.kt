package com.mml.topwho

import android.app.Application
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

class TopWhoApplication: Application() {
    companion object{
        var instances:Application?=null
    }
    override fun onCreate() {
        super.onCreate()
        if (instances==null)
            instances=this
        initUmeng()
    }

    private fun initUmeng(){
        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)
        /**
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:【友盟+】 AppKey
         * 参数3:【友盟+】 Channel
         * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数5:Push推送业务的secret
         */
        UMConfigure.init(this, "5d665603570df35b86000563", "Once", UMConfigure.DEVICE_TYPE_PHONE,null)

        /**
         * 设置日志加密
         * 参数：boolean 默认为false（不加密）
         */
        UMConfigure.setEncryptEnabled(true)
        /**
         * 子进程是否支持自定义事件统计。
         * 参数：boolean 默认不使用
         */
        UMConfigure. setProcessEvent(true)
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL)

    }
}