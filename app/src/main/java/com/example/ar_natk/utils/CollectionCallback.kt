package com.example.ar_natk.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.ar_natk.data.models.ItemModel

class CollectionCallback : DiffUtil.ItemCallback<ItemModel>() {
    override fun areItemsTheSame(oldItem: ItemModel, newItem: ItemModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ItemModel, newItem: ItemModel): Boolean {
        return oldItem == newItem
    }
}