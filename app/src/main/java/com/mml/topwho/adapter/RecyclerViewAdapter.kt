package com.mml.topwho.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import com.mml.topwho.R
import com.mml.topwho.data.AppInfo
import kotlinx.android.synthetic.main.item_recycler_view.view.*


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 19-8-2 下午5:22
 * Description: This is RecyclerViewAdapter
 * Package: com.mml.topwho.adapter
 * Project: TopWho
 */
class RecyclerViewAdapter(val data:List<AppInfo>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(data[position]) {
            holder.itemView.app_name.text=appName
            holder.itemView.app_icon.setImageDrawable(appIcon)
            holder.itemView.app_package_name.text=packageName
            versionName
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }
}