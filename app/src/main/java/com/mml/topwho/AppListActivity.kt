package com.mml.topwho

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_app_list.*

class AppListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)
        text.text=packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES) .sortByDescending { componentName } .toString()
    }
}
