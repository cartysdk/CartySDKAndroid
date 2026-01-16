package io.carty.bidad.demo.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import coil.load
import io.carty.bidad.demo.R
import io.carty.bidad.demo.databinding.ActivityNativeBinding
import io.carty.bidad.demo.databinding.LayoutNativeBinding
import io.carty.bidad.sdk.openapi.CTAdError
import io.carty.bidad.sdk.openapi.CTAdRequest
import io.carty.bidad.sdk.openapi.CTBaseAd
import io.carty.bidad.sdk.openapi.nativead.CTNative
import io.carty.bidad.sdk.openapi.nativead.CTNativeAdListener
import io.carty.bidad.sdk.openapi.nativead.CTNativeLoadListener

class NativeActivity : BaseActivity(), CTNativeLoadListener, CTNativeAdListener {

    private lateinit var binding: ActivityNativeBinding
    private lateinit var nativeLayout: View
    private lateinit var nativeBinding: LayoutNativeBinding
    private var ctNative: CTNative? = null
    private val placementId = NATIVE_PLACEMENT_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNativeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 创建原生广告布局并将其添加到容器中
        nativeLayout = layoutInflater.inflate(R.layout.layout_native, null, false)
        nativeBinding = LayoutNativeBinding.bind(nativeLayout)

        binding.loadBtn.setOnClickListener {
            loadAd()
        }
    }

    private fun loadAd() {
        binding.adContainer.removeAllViews()
        val adRequest = CTAdRequest.Builder()
            .setPlacementId(placementId)
            .setMute(true)
            .build()
        ctNative = CTNative(adRequest, this@NativeActivity)
        ctNative?.apply {
            binding.statusTv.text = "native loading"
            loadAd()
        }
    }

    override fun onLoaded(baseAd: CTBaseAd?) {
        baseAd?.run {
            val nativeInfo = baseAd.nativeInfo
            nativeInfo?.run {
                binding.statusTv.append("\nnative ad loaded")
                binding.statusTv.append("\nadChoiceUrl: ${nativeInfo.adChoiceUrl}")
                binding.statusTv.append("\ntitle: ${nativeInfo.title}")
                binding.statusTv.append("\nsubTitle: ${nativeInfo.subTitle}")
                binding.statusTv.append("\niconUrl: ${nativeInfo.iconUrl}")
                binding.statusTv.append("\nimageUrl: ${nativeInfo.imageUrl}")
                binding.statusTv.append("\ncallToAction: ${nativeInfo.callToAction}")
                binding.statusTv.append("\nrating: ${nativeInfo.rating}")
                binding.statusTv.append("\nlikes: ${nativeInfo.likes}")
                binding.statusTv.append("\nsponsored: ${nativeInfo.sponsored}")
                binding.statusTv.append("\nlogoUrl: ${nativeInfo.logoUrl}")

                nativeBinding.apply {
                    titleTv.text = nativeInfo.title
                    ratingTv.text = nativeInfo.rating
                    sponsoredTv.text = nativeInfo.sponsored
                    subTitleTv.text = nativeInfo.subTitle
                    ctaBtn.text = nativeInfo.callToAction
                    iconIv.load(nativeInfo.iconUrl)

                    val clickViews = arrayListOf<View>()
                    clickViews.add(titleTv)
                    clickViews.add(ratingTv)
                    clickViews.add(sponsoredTv)
                    clickViews.add(subTitleTv)
                    clickViews.add(ctaBtn)

                    ctNative?.let { nati ->
                        val mediaView = nati.getMediaView(this@NativeActivity)
                        mediaView?.let {
                            clickViews.add(it)
                            mediaViewContainer.addView(
                                it, ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                            )
                        }

                        nati.registerViewForInteraction(
                            nativeBinding.root,
                            clickViews,
                            this@NativeActivity
                        )

                        binding.adContainer.addView(
                            nativeLayout,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onLoadFailed(adError: CTAdError?) {
        binding.statusTv.append(
            "\nnative load failed code ${adError?.errorCode} msg ${adError?.errorMsg}"
        )
    }

    override fun onShown(baseAd: CTBaseAd?) {
        binding.statusTv.append("\nnative onShown")
    }

    override fun onShowFailed(
        baseAd: CTBaseAd?,
        adError: CTAdError?
    ) {
        binding.statusTv.append(
            "\nnative onShowFailed code ${adError?.errorCode} msg ${adError?.errorMsg}"
        )
    }

    override fun onClicked(baseAd: CTBaseAd?) {
        binding.statusTv.append("\nnative onClicked")
    }
}