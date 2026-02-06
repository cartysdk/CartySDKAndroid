package io.carty.bidad.sdk.tradplus;

import android.content.Context;
import android.util.Log;

import com.tradplus.ads.base.adapter.interstitial.TPInterstitialAdapter;

import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.interstitial.CTInterstitial;
import io.carty.bidad.sdk.openapi.interstitial.CTInterstitialAdListener;

public class CTTradPlusInterstitial extends TPInterstitialAdapter implements CTInterstitialAdListener {

    private CTInterstitial mCTInterstitial;

    @Override
    public void loadCustomAd(Context context, Map<String, Object> userParams, Map<String, String> tpParams) {
        Log.i(CTTradPlusMediation.TAG, "loadInterstitial");
        CTTradPlusMediation.init(context, tpParams, new CTAdSdk.CTInitListener() {
            @Override
            public void onInitSuccess() {
                startLoadAd(tpParams);
            }

            @Override
            public void onInitFailed(CTAdError adError) {
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoadFailed(CTTradPlusMediation.getAdError(adError));
                }
            }
        });
    }

    private void startLoadAd(Map<String, String> tpParams) {
        CTAdRequest.Builder builder = CTTradPlusMediation.getAdRequest(tpParams);
        mCTInterstitial = new CTInterstitial(builder.build());
        mCTInterstitial.setInterstitialAdListener(this);
        mCTInterstitial.loadAd();
    }

    @Override
    public void showAd() {
        Log.i(CTTradPlusMediation.TAG, "interstitial showAd");
        if (mCTInterstitial != null) {
            mCTInterstitial.showAd(null);
        }
    }

    @Override
    public boolean isReady() {
        if (mCTInterstitial != null) {
            return mCTInterstitial.isReady();
        }
        return false;
    }

    @Override
    public String getNetworkName() {
        return CTTradPlusMediation.getNetworkName();
    }

    @Override
    public String getNetworkVersion() {
        return CTTradPlusMediation.getNetworkVersion();
    }

    @Override
    public void onLoaded(CTBaseAd baseAd) {
        if (mLoadAdapterListener != null) {
            mLoadAdapterListener.loadAdapterLoaded(null);
        }
    }

    @Override
    public void onLoadFailed(CTAdError adError) {
        if (mLoadAdapterListener != null) {
            mLoadAdapterListener.loadAdapterLoadFailed(CTTradPlusMediation.getAdError(adError));
        }
    }

    @Override
    public void onClosed(CTBaseAd baseAd) {
        if (mShowListener != null) {
            mShowListener.onAdClosed();
        }
    }

    @Override
    public void onShown(CTBaseAd baseAd) {
        if (mShowListener != null) {
            mShowListener.onAdShown();
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mShowListener != null) {
            mShowListener.onAdClicked();
        }
    }
}
