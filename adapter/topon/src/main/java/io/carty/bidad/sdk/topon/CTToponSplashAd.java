package io.carty.bidad.sdk.topon;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup;

import com.secmtp.sdk.core.api.ATInitMediation;
import com.secmtp.sdk.core.api.AdError;
import com.secmtp.sdk.core.api.ErrorCode;
import com.secmtp.sdk.splashad.unitgroup.api.CustomSplashAdapter;

import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.CTGlobalSettings;
import io.carty.bidad.sdk.openapi.splash.CTSplash;
import io.carty.bidad.sdk.openapi.splash.CTSplashAdListener;

public class CTToponSplashAd extends CustomSplashAdapter implements CTSplashAdListener {

    private String mUnitId;
    private CTSplash mCTSplash;

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        CTGlobalSettings.getInstance().setGdpr(isConsent);
        return true;
    }

    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serviceExtras, Map<String, Object> localExtras) {
        mUnitId = ATInitMediation.getStringFromMap(serviceExtras, CTToponMediation.KEY_UNIT_ID);
        Log.i(CTToponMediation.TAG, "loadSplash mUnitId:" + mUnitId);
        CTToponMediation.init(context, serviceExtras, new CTAdSdk.CTInitListener() {
            @Override
            public void onInitSuccess() {
                loadAd();
            }

            @Override
            public void onInitFailed(CTAdError adError) {
                onAdLoadFailed(adError);
            }
        });
    }

    private void loadAd() {
        CTAdRequest adRequest = new CTAdRequest.Builder()
                .setPlacementId(getNetworkPlacementId())
                .build();
        mCTSplash = new CTSplash(adRequest);
        mCTSplash.setSplashAdListener(this);
        mCTSplash.loadAd();
    }

    @Override
    public void show(Activity activity, ViewGroup viewGroup) {
        Log.i(CTToponMediation.TAG, "splash showAd");
        if (mCTSplash != null) {
            mCTSplash.showAd(activity);
        } else {
            onAdShowFailed(ErrorCode.getErrorCode(ErrorCode.adShowError, "", "ctSplash is null"));
        }
    }

    @Override
    public void destory() {
        mLoadListener = null;
        mImpressionListener = null;
    }

    @Override
    public boolean isAdReady() {
        if (mCTSplash != null) {
            return mCTSplash.isReady();
        }
        return false;
    }

    @Override
    public String getNetworkPlacementId() {
        return mUnitId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return CTToponMediation.getNetworkSDKVersion();
    }

    @Override
    public String getNetworkName() {
        return CTToponMediation.getNetworkName();
    }

    @Override
    public void onLoaded(CTBaseAd baseAd) {
        if (mLoadListener != null) {
            mLoadListener.onAdCacheLoaded();
        }
    }

    @Override
    public void onLoadFailed(CTAdError adError) {
        onAdLoadFailed(adError);
    }

    private void onAdLoadFailed(CTAdError adError) {
        Pair<String, String> errorPair = CTToponMediation.getAdError(adError);
        if (mLoadListener != null) {
            mLoadListener.onAdLoadError(errorPair.first, errorPair.second);
        }
    }

    @Override
    public void onClosed(CTBaseAd baseAd) {
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdDismiss();
        }
    }

    @Override
    public void onShown(CTBaseAd baseAd) {
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdShow();
        }
    }

    @Override
    public void onShowFailed(CTBaseAd baseAd, CTAdError adError) {
        Pair<String, String> errorPair = CTToponMediation.getAdError(adError);
        onAdShowFailed(ErrorCode.getErrorCode(ErrorCode.adShowError, errorPair.first, errorPair.second));
    }

    private void onAdShowFailed(AdError adError) {
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdShowFail(adError);
            mImpressionListener.onSplashAdDismiss();
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdClicked();
        }
    }
}
