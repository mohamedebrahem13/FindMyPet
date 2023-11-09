package com.example.petme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petme.databinding.ItemImageBinding

class ImageAdapter(private val imageList: List<String>?) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageList?.get(position)
        if (imageUrl != null) {
            holder.bind(imageUrl)
        }
    }

    override fun getItemCount(): Int {
        return imageList?.size ?: 0
    }

    inner class ImageViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            // Load the image using Glide
            Glide.with(binding.imageView.context)
                .load(imageUrl)
                .into(binding.imageView)

            binding.executePendingBindings()
        }
    }
}
