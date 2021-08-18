package com.mml.topwho

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mml.topwho.adapter.DialogRecyclerViewAdapter
import com.mml.topwho.adapter.RecyclerViewAdapter
import com.mml.topwho.annotatio.FieldOrderAnnotation
import com.mml.topwho.data.AppInfo
import com.mml.topwho.databinding.ActivityAppListBinding
import com.mml.topwho.databinding.DialogAppListItemInfoBinding
import com.mml.topwho.dialog.CustomDialog
import com.mml.topwho.py.CharactersSideBar
import com.mml.topwho.py.PYFactory
import com.mml.topwho.py.StickyHeaderDecoration
import com.umeng.analytics.MobclickAgent
import java.util.*
import kotlin.Comparator
import kotlin.math.ceil
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField


class AppListActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAppListBinding
    lateinit var listApplicationInfo: List<ApplicationInfo>
    lateinit var listPackageInfo: List<PackageInfo>
    private val originDataList = mutableListOf<AppInfo>()
    private val dataSystemList = mutableListOf<AppInfo>()
    private val dataUserList = mutableListOf<AppInfo>()
    private val dataList = mutableListOf<AppInfo>()
    private lateinit var mAdapter: RecyclerViewAdapter
    var ALLPAGES: Int = 0
    var CURRENTPAGE = 0
    var PAGE_SIZE = 10.0
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val fragment: Fragment? = null
            notifyDataSetChanged(item)
            true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppListBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        initData()
        initView()
        AppListTask().execute()
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
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
        val main = Intent("android.intent.action.MAIN")
        main.addCategory("android.intent.category.LAUNCHER")
        val resolveInfos =
            packageManager.queryIntentActivities(main, 0)
        Collections.sort(resolveInfos, ResolveInfo.DisplayNameComparator(packageManager))
        val allList = mutableListOf<AppInfo>()
        listPackageInfo.forEach {
            with(it) {
                val resolveInfo = resolveInfos.find { resolveInfo ->
                    resolveInfo.activityInfo.packageName == packageName
                }
                val appName = applicationInfo.loadLabel(packageManager).toString()
                val packageName = packageName
                val versionName = versionName
                val className = resolveInfo?.activityInfo?.name//applicationInfo.className
                val versionCode = longVersionCode
                val icon = it.applicationInfo.loadIcon(packageManager)
                val flag =
                    applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == ApplicationInfo.FLAG_SYSTEM
                val minSdkVersion = applicationInfo.minSdkVersion
                val targetSdkVersion = applicationInfo.targetSdkVersion
                val sourcePath = applicationInfo.sourceDir
                val dataDir = applicationInfo.dataDir
                val sourceDir = applicationInfo.sourceDir
                allList.add(
                    AppInfo(
                        appName,
                        packageName,
                        versionName,
                        className,
                        versionCode,
                        icon,
                        flag,
                        minSdkVersion,
                        targetSdkVersion,
                        sourcePath,
                        dataDir,
                        sourceDir
                    )
                )
            }
        }
        PYFactory.createPinyinList(allList)
        val list2 = allList.toMutableList().apply {
            sortBy {
                it.firstChar
            }
        }
        originDataList.addAll(list2)
        ALLPAGES = ceil(originDataList.size / PAGE_SIZE).toInt()
        CURRENTPAGE = 0
        log(msg = "ALLPAGES:$ALLPAGES  PAGESIZE:$PAGE_SIZE ALL:${originDataList.size}")
        val user = originDataList.filter { !it.isSystemApp }
        val list = user.toMutableList().apply {
            sortBy {
                it.firstChar
            }
        }
        dataUserList.addAll(list)
        val system = originDataList.filter { it.isSystemApp }
        val list1 = system.toMutableList().apply {
            sortBy {
                it.firstChar
            }
        }
        dataSystemList.addAll(list1)
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
                ALLPAGES = ceil(originDataList.size / PAGE_SIZE).toInt()
                CURRENTPAGE = 0
                dataList.apply {
                    addAll(originDataList)
                }
                showToast("共有${originDataList.size}个app")
            }
            R.id.navigation_user -> {
                dataList.clear()
                ALLPAGES = ceil(dataUserList.size / PAGE_SIZE).toInt()
                CURRENTPAGE = 0
                dataList.apply {
                    addAll(dataUserList)
                }
                showToast("用户有${dataUserList.size}个app")
            }
            R.id.navigation_system -> {
                dataList.clear()
                ALLPAGES = ceil(dataSystemList.size / PAGE_SIZE).toInt()
                CURRENTPAGE = 0
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
        binding.navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        binding.mCharactersSideBar.setPosition(0)
        binding.mCharactersSideBar.setTextDialog(binding.tvCharacter)
        binding.mCharactersSideBar.setOnTouchingLetterChangedListener(object :
            CharactersSideBar.OnTouchingLetterChangedListener {
            override fun onTouchingLetterChanged(position: Int, character: String) {
                dataList.forEachIndexed { index, it ->
                    val firstChar = if (it.firstChar.toString()
                            .matches(Regex("[a-zA-Z]+"))
                    ) it.firstChar.toString() else '#'
                    if (firstChar == character) {
                        binding.mRecyclerView.layoutManager?.scrollToPosition(index)
                    }
                }
            }

        })
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        mAdapter = RecyclerViewAdapter(dataList).apply {
            onItemClickListener = {

                val itemInfo = dataList[it]
                val fieldList = itemInfo.javaClass.kotlin.declaredMemberProperties.toList()
                val map = fieldList.sortedWith(Comparator.comparingInt { m ->
                    m.javaField!!.getAnnotation(FieldOrderAnnotation::class.java)?.order ?: 0
                }).map { kProperty1 ->
                    Pair(
                        kProperty1.name,
                        kProperty1.get(itemInfo)
                    )
                }.toMap()
                val adapter = DialogRecyclerViewAdapter(map)
                val viewBinding = DialogAppListItemInfoBinding.inflate(layoutInflater)
                CustomDialog()
                    .setCustomView(viewBinding.root)
                    .convert { view ->
                        viewBinding.recyclerView.adapter = adapter
                        viewBinding.tvCopy.setOnClickListener {
                            showDebugToast(msg = "tv_copy")
                            //获取剪贴板管理器：
                            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            // 创建普通字符型ClipData
                            val mClipData = ClipData.newPlainText("itemInfo", itemInfo.toString())
                            // 将ClipData内容放到系统剪贴板里。
                            cm.setPrimaryClip(mClipData)
                            showToast("复制到剪切板成功")
                        }
                        viewBinding.tvOpen.setOnClickListener {
                            showDebugToast("tv_open")
                            val packageURI = Uri.parse("package:${itemInfo.packageName}")
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
                            startActivity(intent)

                        }
                    }
                    .setOnDismissCallback { }
                    .show(supportFragmentManager)
            }
            onCharacterChange = {
                binding.mCharactersSideBar.setPosition(it.toString())
            }
        }
        with(binding.mRecyclerView) {
            this.layoutManager = layoutManager
            addItemDecoration(StickyHeaderDecoration(mAdapter))
        }
        with(binding.mSmartRefreshLayout) {
            setOnRefreshListener {
                dataList.clear()
                when ( binding.navView.selectedItemId) {
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
                it.finishRefresh()
            }
        }
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
            binding.progressBar.visible()

        }

        override fun doInBackground(vararg p0: Any?): Boolean {
            initData()
            runOnUiThread {
                binding.mRecyclerView.adapter = mAdapter
            }
            return true
        }

        override fun onPostExecute(result: Boolean?) {
//            super.onPostExecute(result)
            if (result!!) {
                binding.progressBar.gone()
                binding.mSmartRefreshLayout.autoRefresh()
            } else {
                showToast("加载失败！")
            }
        }

    }
}
