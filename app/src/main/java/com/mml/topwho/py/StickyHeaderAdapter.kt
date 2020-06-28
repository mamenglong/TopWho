package com.mml.topwho.py

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * The adapter to assist the [StickyHeaderDecoration] in creating and binding the header views.
 *
 * @param <T> the header view holder
</T> */
interface StickyHeaderAdapter<T : RecyclerView.ViewHolder> {
    /**
     * Returns the header id for the item at the given position.
     *
     * @param childAdapterPosition the item adapter position
     * @return the header id, or [.NO_HEADER] if this item has no header.
     */
    fun getHeaderId(childAdapterPosition: Int): Long

    /**
     * Creates a new header ViewHolder.
     *
     * @param parent the header's view parent, typically the RecyclerView
     * @return a view holder for the created view
     */
    fun onCreateHeaderViewHolder(parent: ViewGroup): T

    fun onFirstHeaderChange(char: Char)

    /**
     * Display the data at the specified position.
     *
     *
     * PLEASE NOTE THE PARAM IS CHILD POSITION!
     * IF YOU WANT TO USE HEADER ID, PLEASE CALL YOUR [.getHeaderId].
     *
     * @param holder the header view holder
     * @param childAdapterPosition the child item position, can be used to retrieve header id.
     */
    fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, childAdapterPosition: Int)

    companion object {
        /**
         * Indicate this item has no corresponding header.
         */
        const val NO_HEADER = -1L
    }
}