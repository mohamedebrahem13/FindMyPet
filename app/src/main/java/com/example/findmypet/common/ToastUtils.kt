package com.example.findmypet.common

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.findmypet.R
import com.google.android.material.snackbar.Snackbar

object ToastUtils {

    // Modify the function signature to include a 'length' parameter with a default value
    fun showCustomToast(context: Context, message: String, parentView: ViewGroup, isSuccess: Boolean, length: Int = Toast.LENGTH_SHORT) {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.custom_toast_layout, parentView, false)

        val imageViewIcon = layout.findViewById<ImageView>(R.id.imageViewIcon)
        imageViewIcon.setImageResource(if (isSuccess) R.drawable.dog_toast else R.drawable.sad_dog)

        val textViewMessage = layout.findViewById<TextView>(R.id.textViewMessage)
        textViewMessage.text = message

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Use Snackbar for API level R and above
            val snackbar = Snackbar.make(parentView, message, length)
            snackbar.show()
        } else {
            // Use custom Toast for API level below R
            val toast = Toast(context)
            toast.duration = length
            toast.view = layout
            toast.show()
        }
    }
}