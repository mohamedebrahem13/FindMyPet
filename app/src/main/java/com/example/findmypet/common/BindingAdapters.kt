package com.example.findmypet.common

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.findmypet.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
object DateConverter {

    @JvmStatic
    @BindingAdapter("android:text")
    fun bindDate(textView: TextView, timestamp: Long) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(timestamp))
        textView.text = formattedDate
    }
}







