package com.mml.topwho.py

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.collection.LongSparseArray
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * sticky header decoration.
 */
open class StickyHeaderDecoration(private val mAdapter: StickyHeaderAdapter<RecyclerView.ViewHolder>) :
    ItemDecoration() {
    private val mHeaderCache: LongSparseArray<RecyclerView.ViewHolder> = LongSparseArray()

    /**
     * {@inheritDoc}
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        var headerHeight = 0
        if (position != RecyclerView.NO_POSITION && hasHeader(position) && shouldShowHeader(position)
        ) {
            headerHeight = getHeader(parent, position).itemView.height
        }
        outRect[0, headerHeight, 0] = 0
    }

    /**
     * {@inheritDoc}
     */
    override fun onDrawOver(
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val count = parent.childCount
        var previousHeaderId: Long = -1
        for (layoutPos in 0 until count) {
            val child = parent.getChildAt(layoutPos)
            val adapterPos = parent.getChildAdapterPosition(child)
            if (adapterPos != RecyclerView.NO_POSITION && hasHeader(adapterPos)) {
                val headerId = mAdapter.getHeaderId(adapterPos)
                if (headerId != previousHeaderId) {
                    val header = getHeader(parent, adapterPos).itemView
                    if (layoutPos == 0) {
                        mAdapter.onFirstHeaderChange(headerId.toChar())
                    }
                    canvas.save()
                    val left = child.left
                    val top = getHeaderTop(parent, child, header, adapterPos, layoutPos)
                    canvas.translate(left.toFloat(), top.toFloat())
                    header.translationX = left.toFloat()
                    header.translationY = top.toFloat()
                    header.draw(canvas)
                    canvas.restore()
                    previousHeaderId = headerId
                }
            }
        }
    }

    /**
     * Decide whether the item header should be shown. Default Rules:
     * 1. the first item header always should;
     * 2. if the item's header is different than last item, it should be shown, otherwise not.
     *
     * @param itemAdapterPosition adapter position, see RecyclerView docs.
     * @return `true` if this header should be shown.
     */
    private fun shouldShowHeader(itemAdapterPosition: Int): Boolean {
        return if (itemAdapterPosition == 0) {
            true
        } else mAdapter.getHeaderId(itemAdapterPosition) != mAdapter.getHeaderId(
            itemAdapterPosition - 1
        )
    }

    /**
     * Check whether header exists on specified position.
     *
     * @return true if this position has a header.
     */
    private fun hasHeader(adapterPosition: Int): Boolean {
        return mAdapter.getHeaderId(adapterPosition) != StickyHeaderAdapter.NO_HEADER
    }

    private fun getHeader(parent: RecyclerView, adapterPosition: Int): RecyclerView.ViewHolder {
        val key = mAdapter.getHeaderId(adapterPosition)
        var holder: RecyclerView.ViewHolder? = mHeaderCache[key]
        if (holder == null) {
            holder = mAdapter.onCreateHeaderViewHolder(parent)
            val header = holder.itemView
            mAdapter.onBindHeaderViewHolder(holder, adapterPosition)
            val widthSpec = View.MeasureSpec.makeMeasureSpec(
                parent.measuredWidth,
                View.MeasureSpec.EXACTLY
            )
            val heightSpec = View.MeasureSpec.makeMeasureSpec(
                parent.measuredHeight,
                View.MeasureSpec.UNSPECIFIED
            )
            val childWidth = ViewGroup.getChildMeasureSpec(
                widthSpec,
                parent.paddingLeft + parent.paddingRight, header.layoutParams.width
            )
            val childHeight = ViewGroup.getChildMeasureSpec(
                heightSpec,
                parent.paddingTop + parent.paddingBottom, header.layoutParams.height
            )
            header.measure(childWidth, childHeight)
            header.layout(0, 0, header.measuredWidth, header.measuredHeight)
            mHeaderCache.put(key, holder)
        }
        return holder
    }

    private fun getHeaderTop(
        parent: RecyclerView,
        child: View,
        header: View,
        adapterPos: Int,
        layoutPos: Int
    ): Int {
        val headerHeight = header.height
        var top = child.y.toInt() - headerHeight
        if (layoutPos == 0) {
            val count = parent.childCount
            val currentId = mAdapter.getHeaderId(adapterPos)
            // find next view with header and compute the offscreen push if needed
            for (i in 1 until count) {
                val adapterPosHere = parent.getChildAdapterPosition(parent.getChildAt(i))
                if (adapterPosHere != RecyclerView.NO_POSITION) {
                    val nextId = mAdapter.getHeaderId(adapterPosHere)
                    if (nextId != currentId) {
                        val next = parent.getChildAt(i)
                        val offset = next.y.toInt() - (headerHeight + getHeader(
                            parent,
                            adapterPosHere
                        ).itemView.height)
                        return if (offset < 0) {
                            offset
                        } else {
                            break
                        }
                    }
                }
            }
            top = Math.max(0, top)
        }
        return top
    }

}