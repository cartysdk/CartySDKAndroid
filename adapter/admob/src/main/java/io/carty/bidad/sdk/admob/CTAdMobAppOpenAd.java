package io.carty.bidad.sdk.admob;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationAppOpenAd;
import com.google.android.gms.ads.mediation.MediationAppOpenAdCallback;
import com.google.android.gms.ads.mediation.MediationAppOpenAdConfiguration;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.splash.CTSplash;
import io.carty.bidad.sdk.openapi.splash.CTSplashAdListener;

public class CTAdMobAppOpenAd implements MediationAppOpenAd, CTSplashAdListener {

    private CTSplash mCTSplash;
    private MediationAdLoadCallback<MediationAppOpenAd, MediationAppOpenAdCallback> mCallback;
    private MediationAppOpenAdCallback mOpenAdCallback;

    public void loadAd(@NonNull MediationAppOpenAdConfiguration mediationAppOpenAdConfiguration,
                       @NonNull MediationAdLoadCallback<MediationAppOpenAd, MediationAppOpenAdCallback> callback) {
        this.mCallback = callback;
        mCTSplash = new CTSplash(CTAdMobAdapter.getAdRequestBuilder(
                mediationAppOpenAdConfiguration.getServerParameters()).build());
        mCTSplash.setSplashAdListener(this);
        mCTSplash.loadAd();
    }

    @Override
    public void showAd(@NonNull Context context) {
        Log.i(CTAdMobAdapter.TAG, "appOpen showAd");
        Activity activity = null;
        if ((context instanceof Activity)) {
            activity = (Activity) context;
        }
        if (mCTSplash != null) {
            mCTSplash.showAd(activity);
        } else {
            if (mOpenAdCallback != null) {
                mOpenAdCallback.onAdFailedToShow(CTAdMobAdapter.getAdError(
                        new CTAdError(CTAdMobConstants.DEFAULT_ERROR_CODE, "context not activity")));
            }
        }
    }

    @Override
    public void onLoaded(CTBaseAd baseAd) {
        mOpenAdCallback = mCallback.onSuccess(this);
    }

    @Override
    public void onLoadFailed(CTAdError adError) {
        if (mCallback != null) {
            mCallback.onFailure(CTAdMobAdapter.getAdError(adError));
        }
    }

    @Override
    public void onClosed(CTBaseAd baseAd) {
        if (mOpenAdCallback != null) {
            mOpenAdCallback.onAdClosed();
        }
        mCTSplash = null;
    }

    @Override
    public void onShown(CTBaseAd baseAd) {
        if (mOpenAdCallback != null) {
            mOpenAdCallback.onAdOpened();
            mOpenAdCallback.reportAdImpression();
        }
    }

    @Override
    public void onShowFailed(CTBaseAd baseAd, CTAdError adError) {
        if (mOpenAdCallback != null) {
            mOpenAdCallback.onAdFailedToShow(CTAdMobAdapter.getAdError(adError));
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mOpenAdCallback != null) {
            mOpenAdCallback.reportAdClicked();
        }
    }
}
