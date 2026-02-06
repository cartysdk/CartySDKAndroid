package io.carty.bidad.sdk.admob;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MediationUtils;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationBannerAd;
import com.google.android.gms.ads.mediation.MediationBannerAdCallback;
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration;

import java.util.ArrayList;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.banner.CTBannerAdListener;
import io.carty.bidad.sdk.openapi.banner.CTBannerView;

public class CTAdMobBannerAd implements MediationBannerAd, CTBannerAdListener {

    private CTBannerView mCTBannerView;
    private MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> mCallback;
    private MediationBannerAdCallback mBannerAdCallback;

    public void loadAd(@NonNull MediationBannerAdConfiguration mediationBannerAdConfiguration,
                       @NonNull MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> callback) {
        this.mCallback = callback;
        Context context = mediationBannerAdConfiguration.getContext();
        ArrayList<AdSize> supportedAdSize = new ArrayList<>();
        supportedAdSize.add(new AdSize(CTAdRequest.AdSize.BANNER_320_50.getWidth(), CTAdRequest.AdSize.BANNER_320_50.getHeight()));
        supportedAdSize.add(new AdSize(CTAdRequest.AdSize.BANNER_320_100.getWidth(), CTAdRequest.AdSize.BANNER_320_100.getHeight()));
        supportedAdSize.add(new AdSize(CTAdRequest.AdSize.BANNER_300_250.getWidth(), CTAdRequest.AdSize.BANNER_300_250.getHeight()));
        AdSize bannerSize = MediationUtils.findClosestSize(context,
                mediationBannerAdConfiguration.getAdSize(), supportedAdSize);
        if (bannerSize == null) {
            mCallback.onFailure(CTAdMobAdapter.getAdError(new CTAdError(CTAdMobConstants.DEFAULT_ERROR_CODE, "banner size error")));
            return;
        }

        CTAdRequest.AdSize cartyAdSize;
        if (bannerSize.equals(AdSize.MEDIUM_RECTANGLE)) {
            cartyAdSize = CTAdRequest.AdSize.BANNER_300_250;
        } else if (bannerSize.equals(AdSize.LARGE_BANNER)) {
            cartyAdSize = CTAdRequest.AdSize.BANNER_320_100;
        } else {
            cartyAdSize = CTAdRequest.AdSize.BANNER_320_50;
        }

        CTAdRequest.Builder builder = CTAdMobAdapter.getAdRequestBuilder(
                mediationBannerAdConfiguration.getServerParameters());
        builder.setAdSize(cartyAdSize);
        mCTBannerView = new CTBannerView(context, builder.build());
        mCTBannerView.setBannerAdListener(this);
        mCTBannerView.loadAd();
    }

    @NonNull
    @Override
    public View getView() {
        Log.i(CTAdMobAdapter.TAG, "getBannerView");
        return mCTBannerView;
    }

    @Override
    public void onLoaded(CTBaseAd baseAd) {
        mBannerAdCallback = mCallback.onSuccess(this);
    }

    @Override
    public void onLoadFailed(CTAdError adError) {
        mCallback.onFailure(CTAdMobAdapter.getAdError(adError));
    }

    @Override
    public void onClosed(CTBaseAd baseAd) {
        if (mBannerAdCallback != null) {
            mBannerAdCallback.onAdClosed();
        }
    }

    @Override
    public void onShown(CTBaseAd baseAd) {
        if (mBannerAdCallback != null) {
            mBannerAdCallback.onAdOpened();
            mBannerAdCallback.reportAdImpression();
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mBannerAdCallback != null) {
            mBannerAdCallback.reportAdClicked();
        }
    }
}
