package io.carty.bidad.sdk.admob;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAd;
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;

import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.reward.CTReward;
import io.carty.bidad.sdk.openapi.reward.CTRewardAdListener;

public class CTAdMobRewardAd implements MediationRewardedAd, CTRewardAdListener {

    private CTReward mCTReward;
    private MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mCallback;
    private MediationRewardedAdCallback mRewardAdCallback;

    public void loadAd(@NonNull MediationRewardedAdConfiguration mediationRewardedAdConfiguration,
                       @NonNull MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> callback) {
        this.mCallback = callback;
        mCTReward = new CTReward(CTAdMobAdapter.getAdRequestBuilder(
                mediationRewardedAdConfiguration.getServerParameters()).build());
        mCTReward.setRewardAdListener(this);
        mCTReward.loadAd();
    }

    @Override
    public void showAd(@NonNull Context context) {
        Log.i(CTAdMobAdapter.TAG, "reward showAd");
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        if (mCTReward != null) {
            mCTReward.showAd(activity);
        }
    }

    @Override
    public void onLoaded(CTBaseAd baseAd) {
        mRewardAdCallback = mCallback.onSuccess(this);
    }

    @Override
    public void onLoadFailed(CTAdError adError) {
        mCallback.onFailure(CTAdMobAdapter.getAdError(adError));
    }

    @Override
    public void onClosed(CTBaseAd baseAd) {
        if (mRewardAdCallback != null) {
            mRewardAdCallback.onAdClosed();
        }
        mRewardAdCallback = null;
    }

    @Override
    public void onShown(CTBaseAd baseAd) {
        if (mRewardAdCallback != null) {
            mRewardAdCallback.onAdOpened();
            mRewardAdCallback.reportAdImpression();
        }
    }

    @Override
    public void onShowFailed(CTBaseAd baseAd, CTAdError adError) {
        if (mRewardAdCallback != null) {
            mRewardAdCallback.onAdFailedToShow(CTAdMobAdapter.getAdError(adError));
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mRewardAdCallback != null) {
            mRewardAdCallback.reportAdClicked();
        }
    }

    @Override
    public void onRewarded(CTBaseAd baseAd, Map<String, Object> rewardMap) {
        if (mRewardAdCallback != null) {
            mRewardAdCallback.onUserEarnedReward();
        }
    }

    @Override
    public void onVideoPlay(CTBaseAd baseAd) {
        if (mRewardAdCallback != null) {
            mRewardAdCallback.onVideoStart();
        }
    }

    @Override
    public void onVideoComplete(CTBaseAd baseAd) {
        if (mRewardAdCallback != null) {
            mRewardAdCallback.onVideoComplete();
        }
    }
}
