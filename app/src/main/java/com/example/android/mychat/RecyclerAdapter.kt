package com.example.android.mychat

import android.content.ClipData
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mychat.databinding.ItemOfRecyclerMessageBinding

class RecyclerAdapter : ListAdapter<User, RecyclerAdapter.ItemHolder>(ItemComparator()) {

    class ItemHolder(private val binding: ItemOfRecyclerMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) = with(binding) {
            textUser.text = user.name
            textMessage.text = user.message
        }

        companion object {
            fun createItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    ItemOfRecyclerMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class ItemComparator: DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.createItem(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
       holder.bind(getItem(position))
    }


}