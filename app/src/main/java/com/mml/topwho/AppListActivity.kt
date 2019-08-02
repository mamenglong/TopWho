package com.mml.topwho

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.mml.topwho.data.AppInfo
import kotlinx.android.synthetic.main.activity_app_list.*
import com.jcodecraeer.xrecyclerview.ProgressStyle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.mml.topwho.adapter.RecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_app_list.view.*


class AppListActivity : AppCompatActivity() {
    lateinit var listApplicationInfo:List<ApplicationInfo>
    lateinit var listPackageInfo:List<PackageInfo>
    private val dataList= mutableListOf<AppInfo>()
    private lateinit var mAdapter:RecyclerViewAdapter
    private lateinit var textMessage: TextView
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val fragment: Fragment? = null
        when (item.itemId) {
            R.id.navigation_home -> {
                textMessage.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                textMessage.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                textMessage.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
//            return@OnNavigationItemSelectedListener loadFragment(fragment)
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)
        initData()
        initView()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun initData(){
        listApplicationInfo=packageManager.getInstalledApplications(0)
        listPackageInfo=packageManager.getInstalledPackages(0)
        listPackageInfo.forEach {
            with(it) {
                val appName = applicationInfo.loadLabel(packageManager).toString()
                val packageName = packageName
                val versionName = versionName
                val versionCode = versionCode
                val icon = it.applicationInfo.loadIcon(packageManager)
                dataList.add(AppInfo(appName,packageName,versionName,versionCode,icon))
            }
        }
        if (listPackageInfo[1].applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
            //非系统应用

        } else {    //系统应用

        }
    }
    private fun initView(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        textMessage = findViewById(R.id.message)
        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        with(mRecyclerView){
            this.layoutManager = layoutManager
            setPullRefreshEnabled(true)
            defaultRefreshHeaderView // get default refresh header view
                .setRefreshTimeVisible(true)  // make refresh time visible,false means hiding
            setRefreshProgressStyle(ProgressStyle.BallPulse)
            setLoadingMoreProgressStyle(ProgressStyle.BallPulse)
            setLoadingListener(object :XRecyclerView.LoadingListener{
                override fun onLoadMore() {
                    //TODO
                    mRecyclerView.loadMoreComplete()
                    mAdapter.notifyDataSetChanged()
                    mRecyclerView.setNoMore(true)
                }

                override fun onRefresh() {
                    //   TODO
                    mAdapter.notifyDataSetChanged()
                    mRecyclerView.refreshComplete()
                }

            })
            addItemDecoration( DividerItemDecoration(this@AppListActivity,DividerItemDecoration.VERTICAL))
        }
        mAdapter= RecyclerViewAdapter(dataList)
        mRecyclerView.adapter=mAdapter
    }
    private fun loadFragment(fragment: Fragment?): Boolean {
        //switching fragment
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            return true
        }
        return false
    }
}
