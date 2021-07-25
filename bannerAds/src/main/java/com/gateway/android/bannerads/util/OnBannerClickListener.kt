package com.gateway.android.bannerads.util

class OnBannerClickListener(private val clickListener: (Int) -> Unit) {
    fun onClick(item: Int) = clickListener(item)
}