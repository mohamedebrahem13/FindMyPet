package com.example.findmypet.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findmypet.R
import com.example.findmypet.databinding.ItemImagePagerBinding
import com.jsibbold.zoomage.ZoomageView

class ImagePagerAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemImagePagerBinding.inflate(inflater, parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size

    inner class ImageViewHolder(private val binding: ItemImagePagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            val zoomageView: ZoomageView = binding.root.findViewById(R.id.imageViewPagerItem)

            // Load image into the Zoom imageView using Glide
            Glide.with(zoomageView.context)
                .load(imageUrl)
                .into(zoomageView)
        }
    }
}