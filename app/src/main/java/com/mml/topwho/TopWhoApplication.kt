package com.mml.topwho

import android.app.Application

class TopWhoApplication: Application() {
    companion object{
        var instances:Application?=null
    }
    override fun onCreate() {
        super.onCreate()
        if (instances==null)
            instances=this
    }
}