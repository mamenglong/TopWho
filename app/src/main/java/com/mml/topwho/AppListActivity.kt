package com.mml.topwho

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.mml.topwho.data.AppInfo
import kotlinx.android.synthetic.main.activity_app_list.*
import com.jcodecraeer.xrecyclerview.ProgressStyle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.mml.topwho.adapter.RecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_app_list.view.*
import kotlin.math.ceil


class AppListActivity : AppCompatActivity() {
    lateinit var listApplicationInfo: List<ApplicationInfo>
    lateinit var listPackageInfo: List<PackageInfo>
    private val originDataList = mutableListOf<AppInfo>()
    private val dataSystemList = mutableListOf<AppInfo>()
    private val dataUserList = mutableListOf<AppInfo>()
    private val dataList = mutableListOf<AppInfo>()
    private lateinit var mAdapter: RecyclerViewAdapter
    lateinit var navView: BottomNavigationView
    var ALLPAGES:Int = 0
    var CURRENTPAGE=0
    var PAGE_SIZE=10.0
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val fragment: Fragment? = null
        notifyDataSetChanged(item)
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)

//        initData()
        navView = findViewById(R.id.nav_view)
        initView()
        AppListTask().execute()
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
    /**
     * @method:initData
     * @author: Created by Menglong Ma
     * @email：mml2015@126.com
     * @date: 2019/8/3 23:02.
     * @parameters:
     * @description: 初始化
     */
    private fun initData() {
        originDataList.clear()
        dataUserList.clear()
        dataSystemList.clear()
        listApplicationInfo = packageManager.getInstalledApplications(0)
        listPackageInfo = packageManager.getInstalledPackages(0)
        listPackageInfo.forEach {
            with(it) {
                val appName = applicationInfo.loadLabel(packageManager).toString()
                val packageName = packageName
                val versionName = versionName
                val className=applicationInfo.className
                val versionCode = versionCode
                val icon = it.applicationInfo.loadIcon(packageManager)
                val flag = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                originDataList.add(AppInfo(appName, packageName, versionName,className,versionCode, icon, flag))
            }
        }
        originDataList.sortByDescending {
            it.appName
        }
        ALLPAGES= ceil(originDataList.size/PAGE_SIZE).toInt()
        CURRENTPAGE=0
        log(msg="ALLPAGES:$ALLPAGES  PAGESIZE:$PAGE_SIZE ALL:${originDataList.size}")
        dataUserList.addAll(originDataList.filter { !it.isSystemApp })
        dataSystemList.addAll(originDataList.filter { it.isSystemApp })
    }
    /**
     * @method: notifyDataSetChanged
     * @author: Created by Menglong Ma
     * @email：mml2015@126.com
     * @date: 2019/8/3 23:01.
     * @parameters: [item]
     * @description: 根据 bottom 选择更新数据
     */
    private fun notifyDataSetChanged(item: MenuItem) {
        when (item.itemId) {
            R.id.navigation_home -> {
                dataList.clear()
                ALLPAGES= ceil(originDataList.size/PAGE_SIZE).toInt()
                CURRENTPAGE=0
                dataList.apply {
                    addAll(originDataList)
                }
                showToast("共有${originDataList.size}个app")
            }
            R.id.navigation_user -> {
                dataList.clear()
                ALLPAGES= ceil(dataUserList.size/PAGE_SIZE).toInt()
                CURRENTPAGE=0
                dataList.apply{
                    addAll(dataUserList)
                }
                    showToast("用户有${dataUserList.size}个app")
            }
            R.id.navigation_system -> {
                dataList.clear()
                ALLPAGES= ceil(dataSystemList.size/PAGE_SIZE).toInt()
                CURRENTPAGE=0
                dataList.apply {
                    addAll(dataSystemList)
                }
                showToast("系统有${dataSystemList.size}个app")
            }
        }
        mAdapter.notifyDataSetChanged()
    }

    private fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        with(mRecyclerView) {
            this.setFootViewText("正在努力加载中","没有更多啦!")
            this.defaultFootView.setLoadingDoneHint("加载完成啦!")
            this.layoutManager = layoutManager
//            setPullRefreshEnabled(true)
            defaultRefreshHeaderView // get default refresh header view
                .setRefreshTimeVisible(true)  // make refresh time visible,false means hiding
            setRefreshProgressStyle(ProgressStyle.BallPulseRise)
            setLoadingMoreProgressStyle(ProgressStyle.BallBeat)
//            setLimitNumberToCallLoadMore(10)
            setLoadingListener(object : XRecyclerView.LoadingListener {
                override fun onLoadMore() {
                    /*           when (navView.selectedItemId) {
                                   R.id.navigation_home -> {
                                       if (dataList.size <= originDataList.size) {
                                           dataList.addAll(originDataList.subList(dataList.size - 1, dataList.size + 10))
                                       } else {
                                           mRecyclerView.setNoMore(true)
                                       }
                                   }
                                   R.id.navigation_system -> {
                                       if (dataList.size <= dataSystemList.size) {
                                           dataList.addAll(dataSystemList.subList(dataList.size - 1, dataList.size + 10))
                                       } else {
                                           mRecyclerView.setNoMore(true)
                                       }
                                   }
                                   R.id.navigation_user -> {
                                       if (dataList.size <= dataUserList.size) {
                                           dataList.addAll(dataUserList.subList(dataList.size - 1, dataList.size + 10))
                                       } else {
                                           mRecyclerView.setNoMore(true)
                                       }
                                   }
                               }*/
                    mRecyclerView.setNoMore(true)
                    mRecyclerView.loadMoreComplete()
                    mAdapter.notifyDataSetChanged()
                }

                override fun onRefresh() {
                    dataList.clear()
                    when (navView.selectedItemId) {
                        R.id.navigation_home -> {
                            dataList.addAll(originDataList)
                        }
                        R.id.navigation_system -> {
                            dataList.addAll(dataSystemList)
                        }
                        R.id.navigation_user -> {
                            dataList.addAll(dataUserList)
                        }
                    }
                    mAdapter.notifyDataSetChanged()
                    mRecyclerView.refreshComplete()
                }

            })
            addItemDecoration(DividerItemDecoration(this@AppListActivity, DividerItemDecoration.VERTICAL))
        }
        mAdapter = RecyclerViewAdapter(dataList)
        mRecyclerView.adapter = mAdapter
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        //switching fragment
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
//                .replace(R.id.fragment_container, fragment)
                .commit()
            return true
        }
        return false
    }

    inner class AppListTask : AsyncTask<Any?, Int, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visible()

        }

        override fun doInBackground(vararg p0: Any?): Boolean {
            initData()
            return true
        }

        override fun onPostExecute(result: Boolean?) {
//            super.onPostExecute(result)
            if (result!!) {
                progressBar.gone()
                mRecyclerView.refresh()
            } else {
                showToast("加载失败！")
            }
        }

    }
}
