package com.gateway.android.bannerads.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.gateway.android.bannerads.R

@BindingAdapter("loadImage")
fun loadImage(imageView: ImageView, imgUrl: String) {
    Glide.with(imageView)
        .asBitmap()
        .load(imgUrl)
        .apply(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
        )
        .into(imageView)
}