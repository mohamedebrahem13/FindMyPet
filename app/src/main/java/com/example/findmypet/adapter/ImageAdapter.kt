package com.example.findmypet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.findmypet.R
import com.example.findmypet.databinding.ItemImageBinding


class ImageAdapter(private val imageList: List<String>?, private val itemClickListener: OnImageClickListener? = null) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolderWithDelete>() {

    interface OnImageClickListener {
        fun onImageClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolderWithDelete {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolderWithDelete(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolderWithDelete, position: Int) {
        val imageUrl = imageList?.get(position)
        imageUrl?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = imageList?.size ?: 0

    inner class ImageViewHolderWithDelete(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val deleteButton = binding.delete

        init {
            binding.delete.setOnClickListener {
                itemClickListener?.onImageClick(adapterPosition)
            }
        }

        fun bind(imageUrl: String) {
            // Load the image using Glide
            if (imageUrl.isEmpty()) {
                binding.imageView.setImageResource(R.drawable.peturl) // Set the placeholder image
            } else {
                loadImage(imageUrl, binding.imageView)
                binding.executePendingBindings()
            }
            deleteButton.visibility = View.VISIBLE
        }
    }

    private fun loadImage(imageUrl: String, imageView: ImageView) {
        val options = RequestOptions()
            .dontAnimate()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_baseline_error_24)

        Glide.with(imageView.context)
            .load(imageUrl)
            .apply(options)
            .into(imageView)
    }
}