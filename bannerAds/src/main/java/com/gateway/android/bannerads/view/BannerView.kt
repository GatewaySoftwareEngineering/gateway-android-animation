package com.gateway.android.bannerads.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.gateway.android.bannerads.R
import com.gateway.android.bannerads.databinding.BannerViewBinding
import com.gateway.android.bannerads.util.OnBannerClickListener

class BannerView(
    context: Context,
    attrs: AttributeSet
) : FrameLayout(context, attrs) {

    private val binding =
        BannerViewBinding.inflate(LayoutInflater.from(context), this, true)

    lateinit var imagesUrl: List<String>
    private var position: Int = 0

    val fadeInDuration: Long
    val fadeOutDuration: Long
    val displayImageDuration: Long
    val nextImageDelay: Long

    private val fadeInHandler: Handler = Handler(Looper.getMainLooper())
    private val fadeInRunnable: Runnable = Runnable {
        fadeInAnimation()
    }

    private val fadeOutHandler: Handler = Handler(Looper.getMainLooper())
    private val fadeOutRunnable: Runnable = Runnable {
        fadeOutAnimation()
    }

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.BannerView)

        fadeInDuration = attributes.getInteger(
            R.styleable.BannerView_fadeInDuration, 500
        ).toLong()
        fadeOutDuration = attributes.getInteger(
            R.styleable.BannerView_fadeOutDuration, 500
        ).toLong()
        displayImageDuration = attributes.getInteger(
            R.styleable.BannerView_displayImageDuration, 3000
        ).toLong()

        nextImageDelay = attributes.getInteger(
            R.styleable.BannerView_nextImageDelay, 0
        ).toLong() + fadeOutDuration

        attributes.recycle()
    }

    fun start() {
        fadeInAnimation()
    }

    private fun fadeInAnimation() {
        binding.let {
            it.url = imagesUrl[position]
            it.position = position
            it.executePendingBindings()
            it.bannerImageView.animate().setDuration(fadeInDuration).alpha(1f).start()
        }
        fadeOutHandler.postDelayed(fadeOutRunnable, displayImageDuration)
    }

    private fun fadeOutAnimation() {
        position = if (imagesUrl.size == (position + 1)) 0 else position.inc()
        binding.bannerImageView.animate().setDuration(fadeOutDuration).alpha(0f).start()

        fadeInHandler.postDelayed(fadeInRunnable, nextImageDelay)
    }

    fun setOnClickListener(clickListener: OnBannerClickListener) {
        binding.let {
            it.clickListener = clickListener
            it.executePendingBindings()
        }
    }
}