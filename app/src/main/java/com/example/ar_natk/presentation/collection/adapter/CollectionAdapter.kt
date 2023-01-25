package com.example.ar_natk.presentation.collection.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ar_natk.data.models.ItemModel
import com.example.ar_natk.databinding.CollectionItemLayoutBinding
import com.example.ar_natk.utils.CollectionCallback
import com.example.ar_natk.utils.toBitmap

class CollectionAdapter(
    private val onItemClick: (Int) -> Unit
) : ListAdapter<ItemModel, CollectionAdapter.CollectionViewHolder>(CollectionCallback()) {

    class CollectionViewHolder(
        private val binding: CollectionItemLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(itemModel: ItemModel) {
            with(binding) {
                ivItemImage.setImageBitmap(itemModel.previewImage?.toBitmap(binding.root.context))
                tvNumber.text = (layoutPosition + 1).toString()
            }
        }
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        getItem(position).let { item ->
            holder.bind(item)
            holder.itemView.setOnClickListener { onItemClick(item.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        return CollectionViewHolder(
            CollectionItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}
