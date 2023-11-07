package com.example.petme.common

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.petme.R

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: String?) {
    if (url.isNullOrEmpty()) {
        imageView.setImageResource(R.drawable.ic_baseline_account_circle_24) // Set the placeholder image
    } else {
        Glide.with(imageView.context)
            .load(url).dontAnimate()
            .placeholder(R.drawable.ic_launcher_background) // Optional: Use a placeholder image
            .error(R.drawable.ic_baseline_error_24) // Optional: Set an error image
            .into(imageView)
    }
}




