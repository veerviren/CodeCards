package com.example.scorecards.ui

import androidx.recyclerview.widget.DiffUtil

class FriendItemDiffCallback : DiffUtil.ItemCallback<Friend>() {

    override fun areItemsTheSame(
        oldItem: Friend,
        newItem: Friend
    ): Boolean = oldItem.friendHandle == newItem.friendHandle

    override fun areContentsTheSame(
        oldItem: Friend, newItem: Friend
    ) = oldItem == newItem

}