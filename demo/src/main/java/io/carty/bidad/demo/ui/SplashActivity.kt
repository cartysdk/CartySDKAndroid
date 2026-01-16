package io.carty.bidad.demo.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import io.carty.bidad.demo.databinding.ActivitySplashBinding
import io.carty.bidad.sdk.openapi.CTAdError
import io.carty.bidad.sdk.openapi.CTAdRequest
import io.carty.bidad.sdk.openapi.CTBaseAd
import io.carty.bidad.sdk.openapi.splash.CTSplash
import io.carty.bidad.sdk.openapi.splash.CTSplashAdListener

class SplashActivity : BaseActivity(), CTSplashAdListener {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var inAnim: Animation
    private lateinit var outAnim: Animation
    private var splash: CTSplash? = null
    private val placementId = SPLASH_PLACEMENT_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loadBtn.setOnClickListener {
            loadAd()
        }

        inAnim = AnimationUtils.loadAnimation(
            this@SplashActivity,
            android.R.anim.fade_in
        )

        outAnim = AnimationUtils.loadAnimation(
            this@SplashActivity,
            android.R.anim.fade_out
        )

        outAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.adLl.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
    }

    fun loadAd() {
        val adRequest = CTAdRequest.Builder()
            .setPlacementId(placementId)
            .setMute(true)
            .setLandscape(false)
            .build()
        splash = CTSplash(adRequest).apply {
            setSplashAdListener(this@SplashActivity)
            binding.statusTv.text = "splash loading"
            loadAd()
        }
    }

    override fun onLoaded(baseAd: CTBaseAd) {
        binding.statusTv.append("\nsplash loaded")
        val splashView = splash?.getSplashView(this@SplashActivity)
        splashView?.apply {
            binding.adLl.visibility = View.VISIBLE
            binding.adContainer.addView(
                this,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            binding.adLl.startAnimation(inAnim)
        } ?: run {
            binding.statusTv.append("\nsplash view is null")
        }
    }

    override fun onLoadFailed(adError: CTAdError?) {
        binding.statusTv.append("\nsplash load failed code ${adError?.errorCode} msg ${adError?.errorMsg}")
    }

    override fun onClosed(baseAd: CTBaseAd) {
        binding.statusTv.append("\nsplash closed")
        binding.adLl.startAnimation(outAnim)
        splash?.destroy()
        splash = null
    }

    override fun onShown(baseAd: CTBaseAd) {
        binding.statusTv.append("\nsplash shown")
    }

    override fun onShowFailed(baseAd: CTBaseAd, adError: CTAdError) {
        binding.statusTv.append("\nsplash show failed code ${adError.errorCode} msg ${adError.errorMsg}")
    }

    override fun onClicked(baseAd: CTBaseAd) {
        binding.statusTv.append("\nsplash clicked")
    }

    override fun onDestroy() {
        super.onDestroy()
        inAnim.setAnimationListener(null)
        outAnim.setAnimationListener(null)
        binding.adLl.clearAnimation()
        splash?.destroy()
    }
}