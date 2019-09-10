package com.mml.topwho.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mml.topwho.R
import com.mml.topwho.data.AppInfo
import kotlinx.android.synthetic.main.dialog_item_recycler_view.view.*
import kotlinx.android.synthetic.main.item_recycler_view.view.*
import kotlin.math.ceil


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 19-8-2 下午5:22
 * Description: This is RecyclerViewAdapter
 * Package: com.mml.topwho.adapter
 * Project: TopWho
 */
class RecyclerViewAdapter(private val data: List<AppInfo>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    var onItemClickListener: (pos: Int) -> Unit = { _ -> }
    private var PAGE: Int = 0
    private var PAGE_SIZE = 10.0

    init {
        val result = data.size / PAGE_SIZE
        PAGE = ceil(result).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(data[position]) {
            holder.itemView.app_name.text = appName
            holder.itemView.app_icon.setImageDrawable(appIcon)
            holder.itemView.app_package_name.text = packageName
            holder.itemView.app_class_name.text = className
            holder.itemView.setOnClickListener {
                onItemClickListener.invoke(position)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

class DialogRecyclerViewAdapter(val data: Map<String, Any>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    var convert: (holder: RecyclerViewAdapter.ViewHolder, position: Int) -> Unit =
        { viewHolder: RecyclerViewAdapter.ViewHolder, i: Int -> }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dialog_item_recycler_view, parent, false)
        return RecyclerViewAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size
    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        holder.itemView.tv_key.text= data.entries.elementAt(position).key
        holder.itemView.tv_value.text= data.entries.elementAt(position).value.toString()

        convert.invoke(holder, position)
    }

}