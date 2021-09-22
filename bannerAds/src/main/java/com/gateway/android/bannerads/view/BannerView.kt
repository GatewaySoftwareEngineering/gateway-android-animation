package com.gateway.android.bannerads.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
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
    var position: Int = 0

    private var fadeInDuration: Long
    private var fadeOutDuration: Long
    private var displayImageDuration: Long
    private var nextImageDelay: Long

    @DrawableRes
    var errorImageRes: Int = R.drawable.ic_broken_image

    @DrawableRes
    var loadingImageRes: Int = R.drawable.loading_img

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
        binding.apply {
            this@BannerView.position.let {
                url = imagesUrl[it]
                position = it
            }

            Glide.with(bannerImageView)
                .asBitmap()
                .load(url)
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(loadingImageRes)
                        .error(errorImageRes)
                )
                .into(bannerImageView)

            bannerImageView.animate().setDuration(fadeInDuration).alpha(1f).start()
            executePendingBindings()
        }
        fadeOutHandler.postDelayed(fadeOutRunnable, displayImageDuration)
    }

    private fun fadeOutAnimation() {
        position = if (imagesUrl.size == (position + 1)) 0 else position.inc()
        binding.bannerImageView.animate().setDuration(fadeOutDuration).alpha(0f).start()

        fadeInHandler.postDelayed(fadeInRunnable, nextImageDelay)
    }

    fun setOnClickListener(clickListener: OnBannerClickListener) {
        binding.apply {
            this.clickListener = clickListener
            executePendingBindings()
        }
    }

    fun animationsPeriod(
        fadeIn: Long = fadeInDuration,
        fadeOut: Long = fadeOutDuration,
        displayImage: Long = displayImageDuration,
        nextImage: Long = nextImageDelay
    ) {
        fadeInDuration = fadeIn
        fadeOutDuration = fadeOut
        displayImageDuration = displayImage
        nextImageDelay = nextImage + fadeOutDuration
    }
}