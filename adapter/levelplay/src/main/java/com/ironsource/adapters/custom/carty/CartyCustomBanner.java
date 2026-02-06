package com.ironsource.adapters.custom.carty;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;
import android.widget.FrameLayout;

import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.adunit.adapter.BaseBanner;
import com.ironsource.mediationsdk.adunit.adapter.listener.BannerAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;

import org.jetbrains.annotations.NotNull;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.banner.CTBannerAdListener;
import io.carty.bidad.sdk.openapi.banner.CTBannerView;

public class CartyCustomBanner extends BaseBanner<CartyCustomAdapter> implements CTBannerAdListener {

    private CTBannerView mCTBannerView;
    private BannerAdListener mListener;

    public CartyCustomBanner(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NotNull AdData adData, @NotNull Activity activity, @NotNull ISBannerSize isBannerSize, @NotNull BannerAdListener listener) {
        Log.i(CartyCustomAdapter.TAG, "load banner");
        this.mListener = listener;
        CTAdRequest.AdSize cartyAdSize;
        if (isBannerSize.getWidth() == ISBannerSize.LARGE.getWidth() && isBannerSize.getHeight() == ISBannerSize.LARGE.getHeight()) {
            cartyAdSize = CTAdRequest.AdSize.BANNER_320_100;
        } else if (isBannerSize.getWidth() == ISBannerSize.RECTANGLE.getWidth() && isBannerSize.getHeight() == ISBannerSize.RECTANGLE.getHeight()) {
            cartyAdSize = CTAdRequest.AdSize.BANNER_300_250;
        } else {
            cartyAdSize = CTAdRequest.AdSize.BANNER_320_50;
        }
        CTAdRequest.Builder builder = CartyCustomAdapter.getAdRequestBuilder(
                adData.getConfiguration());
        builder.setAdSize(cartyAdSize);
        mCTBannerView = new CTBannerView(activity, builder.build());
        mCTBannerView.setBannerAdListener(this);
        mCTBannerView.loadAd();
    }

    @Override
    public void destroyAd(@NotNull AdData adData) {
        Log.i(CartyCustomAdapter.TAG, "destroy banner");
        if (mCTBannerView != null) {
            mCTBannerView.destroy();
        }
    }

    @Override
    public void onLoaded(CTBaseAd baseAd) {
        if (mListener != null) {
            mListener.onAdLoadSuccess(mCTBannerView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    public void onLoadFailed(CTAdError adError) {
        if (mListener != null) {
            Pair<AdapterErrorType, Pair<Integer, String>> adErrorTypePair = CartyCustomAdapter.getAdErrorTypePair(adError);
            mListener.onAdLoadFailed(adErrorTypePair.first, adErrorTypePair.second.first, adErrorTypePair.second.second);
        }
    }

    @Override
    public void onShown(CTBaseAd baseAd) {
        if (mListener != null) {
            mListener.onAdOpened();
        }
    }

    @Override
    public void onShowFailed(CTBaseAd baseAd, CTAdError adError) {
        if (mListener != null) {
            Pair<Integer, String> errorPair = CartyCustomAdapter.getAdErrorPair(adError);
            mListener.onAdShowFailed(errorPair.first, errorPair.second);
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }
}
