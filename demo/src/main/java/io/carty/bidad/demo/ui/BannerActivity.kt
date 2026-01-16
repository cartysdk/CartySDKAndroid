package io.carty.bidad.demo.ui

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import io.carty.bidad.demo.databinding.ActivityBannerBinding
import io.carty.bidad.sdk.openapi.CTAdError
import io.carty.bidad.sdk.openapi.CTAdRequest
import io.carty.bidad.sdk.openapi.CTBaseAd
import io.carty.bidad.sdk.openapi.banner.CTBannerAdListener
import io.carty.bidad.sdk.openapi.banner.CTBannerView

class BannerActivity : BaseActivity(), CTBannerAdListener {

    private lateinit var binding: ActivityBannerBinding
    private var bannerView: CTBannerView? = null
    private val placementId = BANNER_PLACEMENT_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loadBtn.setOnClickListener {
            loadAd(CTAdRequest.AdSize.BANNER_320_50)
        }
        binding.loadBtn1.setOnClickListener {
            loadAd(CTAdRequest.AdSize.BANNER_320_100)
        }
        binding.loadBtn2.setOnClickListener {
            loadAd(CTAdRequest.AdSize.BANNER_300_250)
        }
    }

    private fun loadAd(adSize: CTAdRequest.AdSize) {
        binding.adContainer.removeAllViews()
        bannerView?.destroy()
        val adRequest = CTAdRequest.Builder()
            .setPlacementId(placementId)
            .setMute(true)
            .setAdSize(adSize)
            .build()
        bannerView = CTBannerView(this@BannerActivity, adRequest)
        bannerView?.apply {
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.CENTER

            setBannerAdListener(this@BannerActivity)
            binding.adContainer.addView(
                this,
                params
            )
            binding.statusTv.text = "banner loading"
            loadAd()
        }
    }

    override fun onLoaded(baseAd: CTBaseAd?) {
        binding.statusTv.append("\nbanner loaded")
    }

    override fun onLoadFailed(adError: CTAdError?) {
        binding.statusTv.append("\nbanner load failed code ${adError?.errorCode} msg ${adError?.errorMsg}")
    }

    override fun onShown(baseAd: CTBaseAd?) {
        binding.statusTv.append("\nbanner onShown")
    }

    override fun onShowFailed(
        baseAd: CTBaseAd?,
        adError: CTAdError?
    ) {
        binding.statusTv.append("\nbanner onShowFailed code ${adError?.errorCode} msg ${adError?.errorMsg}")
    }

    override fun onClicked(baseAd: CTBaseAd?) {
        binding.statusTv.append("\nbanner onClicked")
    }

    override fun onClosed(baseAd: CTBaseAd?) {
        binding.statusTv.append("\nbanner onClosed")
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerView?.destroy()
    }
}