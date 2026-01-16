package io.carty.bidad.demo

import android.app.Application
import io.carty.bidad.demo.ui.showToast
import io.carty.bidad.sdk.openapi.CTAdConfig
import io.carty.bidad.sdk.openapi.CTAdError
import io.carty.bidad.sdk.openapi.CTAdSdk

class DemoApplication : Application() {

    companion object {
        const val APP_ID = "6001923"
    }

    override fun onCreate() {
        super.onCreate()
    }

    fun initSdk() {
        val adConfig = CTAdConfig.Builder().apply {
            setAppId(APP_ID)
            setDebug(BuildConfig.DEBUG)
        }.build()
        CTAdSdk.init(this, adConfig, object : CTAdSdk.CTInitListener {
            override fun onInitSuccess() {
                showToast("init sdk success")
            }

            override fun onInitFailed(adError: CTAdError) {
                showToast("init sdk failed code ${adError.errorCode} msg ${adError.errorMsg}")
            }
        })
    }
}