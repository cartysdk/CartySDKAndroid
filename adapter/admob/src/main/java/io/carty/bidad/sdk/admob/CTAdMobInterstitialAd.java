package io.carty.bidad.sdk.admob;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationInterstitialAd;
import com.google.android.gms.ads.mediation.MediationInterstitialAdCallback;
import com.google.android.gms.ads.mediation.MediationInterstitialAdConfiguration;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.interstitial.CTInterstitial;
import io.carty.bidad.sdk.openapi.interstitial.CTInterstitialAdListener;

public class CTAdMobInterstitialAd implements MediationInterstitialAd, CTInterstitialAdListener {

    private CTInterstitial mCTInterstitial;
    private MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback> mCallback;
    private MediationInterstitialAdCallback mInterstitialAdCallback;

    public void loadAd(@NonNull MediationInterstitialAdConfiguration mediationInterstitialAdConfiguration,
                       @NonNull MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback> callback) {
        this.mCallback = callback;
        mCTInterstitial = new CTInterstitial(CTAdMobAdapter.getAdRequestBuilder(
                mediationInterstitialAdConfiguration.getServerParameters()).build());
        mCTInterstitial.setInterstitialAdListener(this);
        mCTInterstitial.loadAd();
    }

    @Override
    public void showAd(@NonNull Context context) {
        Log.i(CTAdMobAdapter.TAG, "interstitial showAd");
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        if (mCTInterstitial != null) {
            mCTInterstitial.showAd(activity);
        }
    }

    @Override
    public void onLoaded(CTBaseAd baseAd) {
        mInterstitialAdCallback = mCallback.onSuccess(this);
    }

    @Override
    public void onLoadFailed(CTAdError adError) {
        mCallback.onFailure(CTAdMobAdapter.getAdError(adError));
    }

    @Override
    public void onClosed(CTBaseAd baseAd) {
        if (mInterstitialAdCallback != null) {
            mInterstitialAdCallback.onAdClosed();
        }
        mInterstitialAdCallback = null;
    }

    @Override
    public void onShown(CTBaseAd baseAd) {
        if (mInterstitialAdCallback != null) {
            mInterstitialAdCallback.onAdOpened();
            mInterstitialAdCallback.reportAdImpression();
        }
    }

    @Override
    public void onShowFailed(CTBaseAd baseAd, CTAdError adError) {
        if (mInterstitialAdCallback != null) {
            mInterstitialAdCallback.onAdFailedToShow(CTAdMobAdapter.getAdError(adError));
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mInterstitialAdCallback != null) {
            mInterstitialAdCallback.reportAdClicked();
        }
    }
}
