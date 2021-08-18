package com.mml.topwho.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mml.topwho.R
import com.mml.topwho.data.AppInfo
import com.mml.topwho.databinding.AppListItemHeaderBinding
import com.mml.topwho.databinding.DialogItemRecyclerViewBinding
import com.mml.topwho.databinding.ItemRecyclerViewBinding
import com.mml.topwho.extSetVisibility
import com.mml.topwho.py.StickyHeaderAdapter
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
    RecyclerView.Adapter<ViewHolder<ItemRecyclerViewBinding>>(),
    StickyHeaderAdapter<RecyclerView.ViewHolder> {
    var onItemClickListener: (pos: Int) -> Unit = { _ -> }
    var onCharacterChange: (Char) -> Unit = {}
    private var PAGE: Int = 0
    private var PAGE_SIZE = 10.0

    init {
        val result = data.size / PAGE_SIZE
        PAGE = ceil(result).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ItemRecyclerViewBinding> {
        val binding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder<ItemRecyclerViewBinding>, position: Int) {
        with(data[position]) {
            holder.binding.appName.text = appName
            holder.binding.appIcon.setImageDrawable(appIcon)
            holder.binding.appPackageName.text = packageName
            holder.binding.appClassName.text = className
            holder.itemView.setOnClickListener {
                onItemClickListener.invoke(position)
            }
        }
    }

    override fun getHeaderId(childAdapterPosition: Int): Long {
        val firstChar: Char = data[childAdapterPosition].firstChar
        val str = firstChar.toString()
        return if (str.matches(Regex("[a-zA-Z]+"))) firstChar.toLong() else '#'.toLong()
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): ViewHolder<AppListItemHeaderBinding> {
        return ViewHolder(AppListItemHeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun onBindHeaderViewHolder(
        holder: RecyclerView.ViewHolder,
        childAdapterPosition: Int
    ) {
        holder as ViewHolder<AppListItemHeaderBinding>
        val firstChar: Char = data[childAdapterPosition].firstChar
        val str = firstChar.toString()
        holder.binding.tvHeader.text =
            if (str.matches(Regex("[a-zA-Z]+"))) firstChar.toString() else '#'.toString()
    }

    override fun onFirstHeaderChange(char: Char) {
        onCharacterChange.invoke(char)
    }

}
class ViewHolder<T:ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

class DialogRecyclerViewAdapter(val data: Map<String, Any?>) :
    RecyclerView.Adapter<ViewHolder<DialogItemRecyclerViewBinding>>() {
    var convert: (holder: ViewHolder<DialogItemRecyclerViewBinding>, position: Int) -> Unit =
        { viewHolder: ViewHolder<DialogItemRecyclerViewBinding>, i: Int -> }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<DialogItemRecyclerViewBinding> {
        val view = DialogItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return  ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size
    override fun onBindViewHolder(holder: ViewHolder<DialogItemRecyclerViewBinding>, position: Int) {
        holder.binding.tvKey.text = data.entries.elementAt(position).key
        holder.binding.tvValue.text = data.entries.elementAt(position).value.toString()
        if (position == 5)
            holder.binding.ivIcon.apply {
                holder.binding.tvValue.extSetVisibility(false)
                extSetVisibility(true)
                setImageDrawable(data.entries.elementAt(position).value as Drawable?)
            }
        convert.invoke(holder, position)
    }

}