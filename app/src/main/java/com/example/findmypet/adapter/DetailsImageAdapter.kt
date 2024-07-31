package com.example.findmypet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.findmypet.R

class DetailsImageAdapter(private val imageList: List<String>?, private val itemClickListener: OnDetailsImageClickListener? = null) : RecyclerView.Adapter<DetailsImageAdapter.ImageViewHolder>() {

    interface OnDetailsImageClickListener {
        fun onDetailsImageClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_for_details, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageList?.get(position)
        imageUrl?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return imageList?.size ?: 0
    }

    inner class ImageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imageView)

        init {
            imageView.setOnClickListener {
                itemClickListener?.onDetailsImageClick(adapterPosition)
                // Perform the action you want when an image is clicked in this adapter
            }
        }

        fun bind(imageUrl: String) {
            // Load the image using Glide into the imageView
            if (imageUrl.isEmpty()) {
                imageView.setImageResource(R.drawable.ic_baseline_error_24) // Set the placeholder image
            } else {
                loadImage(imageUrl, imageView)
            }
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