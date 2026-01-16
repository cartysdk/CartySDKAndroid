package io.carty.bidad.demo.ui

import android.os.Bundle
import io.carty.bidad.demo.databinding.ActivityInterstitialBinding
import io.carty.bidad.sdk.openapi.CTAdError
import io.carty.bidad.sdk.openapi.CTAdRequest
import io.carty.bidad.sdk.openapi.CTBaseAd
import io.carty.bidad.sdk.openapi.interstitial.CTInterstitial
import io.carty.bidad.sdk.openapi.interstitial.CTInterstitialAdListener

class InterstitialActivity : BaseActivity(), CTInterstitialAdListener {
    private lateinit var binding: ActivityInterstitialBinding
    private var ctInterstitial: CTInterstitial? = null
    private val placementId = INTERSTITIAL_PLACEMENT_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterstitialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loadBtn.setOnClickListener {
            loadAd()
        }
        binding.readyBtn.setOnClickListener {
            isReady()
        }
        binding.showBtn.setOnClickListener {
            if (ctInterstitial == null) {
                showToast("please loadAd")
                return@setOnClickListener
            }
            if (isReady()) {
                showAd()
            }
        }
    }

    private fun loadAd() {
        val adRequest = CTAdRequest.Builder()
            .setPlacementId(placementId)
            .setMute(true)
            .build()
        ctInterstitial = CTInterstitial(adRequest)
        ctInterstitial?.apply {
            setInterstitialAdListener(this@InterstitialActivity)
            binding.statusTv.text = "interstitial loading"
            loadAd()
        }
    }

    private fun isReady(): Boolean {
        val ready = ctInterstitial?.isReady ?: false
        showToast("interstitial isReady $ready")
        return ready
    }

    private fun showAd() {
        ctInterstitial?.showAd(this)
    }

    override fun onLoaded(baseAd: CTBaseAd?) {
        binding.statusTv.append("\ninterstitial loaded")
    }

    override fun onLoadFailed(adError: CTAdError?) {
        binding.statusTv.append("\ninterstitial load failed code ${adError?.errorCode} msg ${adError?.errorMsg}")
    }

    override fun onShown(baseAd: CTBaseAd?) {
        binding.statusTv.append("\ninterstitial onShown")
    }

    override fun onShowFailed(
        baseAd: CTBaseAd?,
        adError: CTAdError?
    ) {
        binding.statusTv.append("\ninterstitial onShowFailed code ${adError?.errorCode} msg ${adError?.errorMsg}")
    }

    override fun onClicked(baseAd: CTBaseAd?) {
        binding.statusTv.append("\ninterstitial onClicked")
    }

    override fun onClosed(baseAd: CTBaseAd?) {
        binding.statusTv.append("\ninterstitial onClosed")
        ctInterstitial = null
    }
}