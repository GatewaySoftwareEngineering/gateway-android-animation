package com.gateway.android.bannerads.view

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
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

    var fadeInDuration: Long
    var fadeOutDuration: Long
    var displayImageDuration: Long
    var nextImageDelay: Long
    private var foreground: Int
    var scaleType: Int

    var fadeInListener: ((Int) -> Unit)? = null
    var fadeOutListener: ((Int) -> Unit)? = null

    fun onFadeInListener(fadeInListener: (Int) -> Unit) {
        this.fadeInListener = fadeInListener
    }

    fun onFadeOutListener(fadeOutListener: (Int) -> Unit) {
        this.fadeOutListener = fadeOutListener
    }

    @DrawableRes
    var errorImageRes: Int = R.drawable.ic_broken_image

    @DrawableRes
    var loadingImageRes: Int = R.drawable.loading_img

    private val fadeInHandler: Handler = Handler(Looper.getMainLooper())
    private val fadeInRunnable: Runnable = Runnable {
        fadeInAnimation()
        fadeInListener?.invoke(position)
    }

    private val fadeOutHandler: Handler = Handler(Looper.getMainLooper())
    private val fadeOutRunnable: Runnable = Runnable {
        fadeOutAnimation()
        fadeOutListener?.invoke(position)
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.BannerView).apply {
            try {
                fadeInDuration = getInteger(
                    R.styleable.BannerView_fadeInDuration, 500
                ).toLong()
                fadeOutDuration = getInteger(
                    R.styleable.BannerView_fadeOutDuration, 500
                ).toLong()
                displayImageDuration = getInteger(
                    R.styleable.BannerView_displayImageDuration, 3000
                ).toLong()

                nextImageDelay = getInteger(
                    R.styleable.BannerView_nextImageDelay, 0
                ).toLong() + fadeOutDuration

                foreground = getResourceId(R.styleable.BannerView_foreground, -1)

                scaleType = getInt(R.styleable.BannerView_scaleType, -1)
            } finally {
                recycle()
            }
        }

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (foreground >= 0)
            setForeground(foreground)
        if (scaleType >= 0) {
            val type = when (scaleType) {
                0 -> ImageView.ScaleType.MATRIX
                1 -> ImageView.ScaleType.FIT_XY
                2 -> ImageView.ScaleType.FIT_START
                3 -> ImageView.ScaleType.FIT_CENTER
                4 -> ImageView.ScaleType.FIT_END
                5 -> ImageView.ScaleType.CENTER
                6 -> ImageView.ScaleType.CENTER_CROP
                7 -> ImageView.ScaleType.CENTER_INSIDE
                else -> ImageView.ScaleType.FIT_CENTER
            }
            setScaleType(type)
        }
    }

    fun start() {
        fadeInAnimation()
        fadeInListener?.invoke(position)
    }

    fun stop() {
        fadeInHandler.removeCallbacks(fadeInRunnable)
        fadeOutHandler.removeCallbacks(fadeOutRunnable)
    }

    private fun fadeInAnimation() {
        binding.apply {
            this@BannerView.position.let {
                url = imagesUrl[it]
                position = it
            }

            try {
                Glide.with(context.applicationContext)
                    .asBitmap()
                    .load(url)
                    .apply(
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(loadingImageRes)
                            .error(errorImageRes)
                    )
                    .into(bannerImageView)
            } catch (e: Exception) {
                e.printStackTrace()
            }

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

    fun setForeground(@DrawableRes resourceId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.root.foreground =
                ResourcesCompat.getDrawable(resources, resourceId, null)
        }
    }

    fun setScaleType(type: ImageView.ScaleType) {
        binding.bannerImageView.scaleType = type
    }
}