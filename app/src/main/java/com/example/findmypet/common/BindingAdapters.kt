package com.example.findmypet.common

import android.widget.ImageView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.findmypet.R

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


@BindingAdapter("postImageUrl")
fun postImage(imageView: ImageView, url: String?) {
    if (url.isNullOrEmpty()) {
        imageView.setImageResource(R.drawable.peturl) // Set the placeholder image
    } else {
        Glide.with(imageView.context)
            .load(url).dontAnimate()
            .placeholder(R.drawable.ic_launcher_background) // Optional: Use a placeholder image
            .error(R.drawable.ic_baseline_error_24) // Optional: Set an error image
            .into(imageView)
    }
}

//for spinner in the edit post
@BindingAdapter("selectedCity")
fun Spinner.setSelectedCity(city: String?) {
    city?.let { selectedCity ->
        val citiesArray = resources.getStringArray(R.array.egypt_cities)
        val cityPosition = citiesArray.indexOf(selectedCity)
        setSelection(cityPosition)
    }
}






