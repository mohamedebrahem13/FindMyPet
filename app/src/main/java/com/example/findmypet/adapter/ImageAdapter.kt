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


class ImageAdapter(private val imageList: List<String>?, private val itemClickListener: OnImageClickListener? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_WITH_DELETE = 1
        private const val VIEW_TYPE_WITHOUT_DELETE = 2
    }

    interface OnImageClickListener {
        fun onImageClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_WITH_DELETE) {
            val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ImageViewHolderWithDelete(binding)
        } else {
            val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ImageViewHolderWithoutDelete(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imageUrl = imageList?.get(position)
        if (imageUrl != null) {
            when (holder) {
                is ImageViewHolderWithDelete -> holder.bind(imageUrl)
                is ImageViewHolderWithoutDelete -> holder.bind(imageUrl)
            }
        }
    }

    override fun getItemCount(): Int {
        return imageList?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemClickListener != null) {
            VIEW_TYPE_WITH_DELETE
        } else {
            VIEW_TYPE_WITHOUT_DELETE
        }
    }

    inner class ImageViewHolderWithDelete(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
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

    inner class ImageViewHolderWithoutDelete(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            // Load the image using Glide
            if (imageUrl.isEmpty()) {
                binding.imageView.setImageResource(R.drawable.peturl) // Set the placeholder image
            } else {
                loadImage(imageUrl, binding.imageView)
                binding.executePendingBindings()
            }
            binding.delete.visibility = View.GONE
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
