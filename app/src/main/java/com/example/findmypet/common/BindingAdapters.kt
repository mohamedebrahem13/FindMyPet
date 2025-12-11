package com.example.findmypet.common

import android.text.TextUtils
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
        imageView.setImageResource(R.drawable.ic_profile_place_holder)
    } else {
        Glide.with(imageView.context)
            .load(url).dontAnimate()
            .placeholder(R.drawable.ic_profile_place_holder)
            .error(R.drawable.ic_baseline_error_24)
            .into(imageView)
    }
}
@BindingAdapter("formattedName")
fun setFormattedName(textView: TextView, nickname: String?) {
    if (!TextUtils.isEmpty(nickname)) {
        val names = nickname!!.split(" ")
        val firstName = names[0]
        val capitalizedFirstName = firstName.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        }
        textView.text = capitalizedFirstName
    } else {
        textView.text = ""
    }
}


@BindingAdapter("postImageUrl")
fun postImage(imageView: ImageView, url: String?) {
    if (url.isNullOrEmpty()) {
        imageView.setImageResource(R.drawable.ic_baseline_error_24)
    } else {
        Glide.with(imageView.context)
            .load(url).dontAnimate()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_baseline_error_24)
            .into(imageView)
    }
}
object DateConverter {

    @JvmStatic
    @BindingAdapter("android:text")
    fun bindDate(textView: TextView, timestamp: Long?) {
        timestamp ?: return // Null check
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        val formattedDate = dateFormat.format(Date(timestamp))
        textView.text = formattedDate
    }
    @JvmStatic
    @BindingAdapter("formattedDate")
    fun bindDate(textView: TextView, date: Date?) {
        date ?: return
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        val formattedDate = dateFormat.format(date)
        textView.text = formattedDate
    }
}






