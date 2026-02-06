package io.carty.bidad.sdk.tradplus;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.tradplus.ads.base.adapter.splash.TPSplashAdapter;

import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.splash.CTSplash;
import io.carty.bidad.sdk.openapi.splash.CTSplashAdListener;

public class CTTradPlusSplash extends TPSplashAdapter implements CTSplashAdListener {

    private CTSplash mCTSplash;
    private Context mContext;

    @Override
    public void loadCustomAd(Context context, Map<String, Object> userParams, Map<String, String> tpParams) {
        Log.i(CTTradPlusMediation.TAG, "load splashAd");
        this.mContext = context;
        CTTradPlusMediation.init(context, tpParams, new CTAdSdk.CTInitListener() {
            @Override
            public void onInitSuccess() {
                Log.i(CTTradPlusMediation.TAG, "init success");
                startLoadAd(tpParams);
            }

            @Override
            public void onInitFailed(CTAdError adError) {
                Log.i(CTTradPlusMediation.TAG, "init failed");
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoadFailed(CTTradPlusMediation.getAdError(adError));
                }
            }
        });
    }

    private void startLoadAd(Map<String, String> tpParams) {
        CTAdRequest.Builder builder = CTTradPlusMediation.getAdRequest(tpParams);
        mCTSplash = new CTSplash(builder.build());
        mCTSplash.setSplashAdListener(this);
        mCTSplash.loadAd();
    }

    @Override
    public void showAd() {
        Log.i(CTTradPlusMediation.TAG, "splash showAd");
        Activity activity = null;
        if (mContext instanceof Activity) {
            activity = (Activity) mContext;
        }
        if (mCTSplash != null) {
            mCTSplash.showAd(activity);
        }
    }

    @Override
    public void clean() {
    }

    @Override
    public boolean isReady() {
        if (mCTSplash != null) {
            return mCTSplash.isReady();
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
