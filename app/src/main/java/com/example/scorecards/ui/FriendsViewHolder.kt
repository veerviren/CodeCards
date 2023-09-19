package com.example.scorecards.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.scorecards.R
import com.example.scorecards.databinding.RecyclerViewItemBinding

class FriendsViewHolder(
    private val itemBinding: RecyclerViewItemBinding,
    val friendlistAdapter: FriendListAdapter
) : RecyclerView.ViewHolder(itemBinding.root){

    fun bind(friend: Friend) {
        itemBinding.apply {

            friendHandle.text = friend.friendHandle
            friendRating.text = friend.friendRating

            Glide.with(friendAvator)
                .load(friend.friendAvatar)
                .placeholder(R.drawable.loading_effect)
                .error(R.drawable.error)
                .into(friendAvator)

            deleteFriend.setOnClickListener {
                friendlistAdapter.onDelete.invoke(friendHandle.text.toString())
            }
        }
    }
}