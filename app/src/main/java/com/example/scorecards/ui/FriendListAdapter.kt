package com.example.scorecards.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.scorecards.R
import com.example.scorecards.databinding.RecyclerViewItemBinding

class FriendListAdapter(
      val onDelete: (Friend) -> Unit
) : ListAdapter<Friend, FriendsViewHolder>(FriendItemDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) =  FriendsViewHolder(
            itemBinding = RecyclerViewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ),
            friendlistAdapter = this
        )

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val item = getItem(position)
        return holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        return  R.layout.recycler_view_item
    }
}