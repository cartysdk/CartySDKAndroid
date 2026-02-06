package com.ironsource.adapters.custom.carty;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo;
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.reward.CTReward;
import io.carty.bidad.sdk.openapi.reward.CTRewardAdListener;

public class CartyCustomRewardedVideo extends BaseRewardedVideo<CartyCustomAdapter> implements CTRewardAdListener {

    private CTReward mCTReward;
    private RewardedVideoAdListener mListener;

    public CartyCustomRewardedVideo(@NotNull NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NotNull AdData adData, @NotNull Context context, @NotNull RewardedVideoAdListener listener) {
        Log.i(CartyCustomAdapter.TAG, "load reward");
        this.mListener = listener;
        mCTReward = new CTReward(CartyCustomAdapter.getAdRequestBuilder(
                adData.getConfiguration()).build());
        mCTReward.setRewardAdListener(this);
        mCTReward.loadAd();
    }

    @Override
    public void showAd(@NotNull AdData adData, @NotNull Activity activity, @NotNull RewardedVideoAdListener listener) {
        Log.i(CartyCustomAdapter.TAG, "reward showAd");
        if (mCTReward != null) {
            mCTReward.showAd(activity);
        }
    }

    @Override
    public boolean isAdAvailable(@NotNull AdData adData) {
        if (mCTReward != null) {
            return mCTReward.isReady();
        }
        return false;
    }

    @Override
    public void destroyAd(@NotNull AdData adData) {
        // do Nothing
    }

    @Override
    public void onLoaded(CTBaseAd baseAd) {
        if (mListener != null) {
            mListener.onAdLoadSuccess();
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
            mListener.onAdVisible();
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

    @Override
    public void onClosed(CTBaseAd baseAd) {
        if (mListener != null) {
            mListener.onAdClosed();
        }
    }

    @Override
    public void onRewarded(CTBaseAd baseAd, Map<String, Object> rewardMap) {
        if (mListener != null) {
            mListener.onAdRewarded(rewardMap);
        }
    }

    @Override
    public void onVideoPlay(CTBaseAd baseAd) {
        if (mListener != null) {
            mListener.onAdStarted();
        }
    }

    @Override
    public void onVideoComplete(CTBaseAd baseAd) {
        if (mListener != null) {
            mListener.onAdEnded();
        }
    }
}
