package com.example.task_2.utils.ext


import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.task_2.R

fun ImageView.loadImage(image: String) {
    Glide.with(this)
        .load(image)
        .circleCrop()
        .placeholder(R.drawable.ic_user_photo)
        .error(R.drawable.ic_user_photo)
        .into(this)
}
