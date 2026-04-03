package io.carty.bidad.sdk.topon;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.secmtp.sdk.core.api.ATBiddingListener;
import com.secmtp.sdk.core.api.ATInitMediation;
import com.secmtp.sdk.rewardvideo.unitgroup.api.CustomRewardVideoAdapter;

import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.CTGlobalSettings;
import io.carty.bidad.sdk.openapi.reward.CTReward;
import io.carty.bidad.sdk.openapi.reward.CTRewardAdListener;

public class CTToponRewardAd extends CustomRewardVideoAdapter implements CTRewardAdListener {

    private String mUnitId;
    private CTReward mCTReward;
    private boolean mC2SBidding;
    private ATBiddingListener mBiddingListener;

    @Override
    public boolean startBiddingRequest(Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra, ATBiddingListener biddingListener) {
        Log.i(CTToponMediation.TAG, "reward startBiddingRequest");
        mC2SBidding = true;
        this.mBiddingListener = biddingListener;
        loadCustomNetworkAd(context, serverExtra, localExtra);
        return true;
    }

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        CTGlobalSettings.getInstance().setGdpr(isConsent);
        return true;
    }

    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serviceExtras, Map<String, Object> localExtras) {
        mUnitId = ATInitMediation.getStringFromMap(serviceExtras, CTToponMediation.KEY_UNIT_ID);
        Log.i(CTToponMediation.TAG, "loadReward mUnitId:" + mUnitId);
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
        mCTReward = new CTReward(adRequest);
        mCTReward.setRewardAdListener(this);
        mCTReward.loadAd();
    }

    @Override
    public void show(Activity activity) {
        Log.i(CTToponMediation.TAG, "reward showAd");
        if (mCTReward != null) {
            mCTReward.showAd(activity);
        }
    }

    @Override
    public void destory() {
        mLoadListener = null;
        mImpressionListener = null;
    }

    @Override
    public boolean isAdReady() {
        if (mCTReward != null) {
            return mCTReward.isReady();
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
        if (mC2SBidding) {
            CTToponMediation.onC2SBiddingSuccess(mCTReward, baseAd, mBiddingListener, null);
        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdCacheLoaded();
            }
        }
    }

    @Override
    public void onLoadFailed(CTAdError adError) {
        onAdLoadFailed(adError);
    }

    private void onAdLoadFailed(CTAdError adError) {
        Pair<String, String> errorPair = CTToponMediation.getAdError(adError);
        if (mC2SBidding) {
            CTToponMediation.onC2SBiddingFailed(adError, mBiddingListener);
        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(errorPair.first, errorPair.second);
            }
        }
    }

    @Override
    public void onClosed(CTBaseAd baseAd) {
        if (mImpressionListener != null) {
            mImpressionListener.onRewardedVideoAdClosed();
        }
    }

    @Override
    public void onRewarded(CTBaseAd baseAd, Map<String, Object> rewardMap) {
        if (mImpressionListener != null) {
            mImpressionListener.onReward();
        }
    }

    @Override
    public void onShown(CTBaseAd baseAd) {
        if (mImpressionListener != null) {
            mImpressionListener.onRewardedVideoAdPlayStart();
        }
    }

    @Override
    public void onShowFailed(CTBaseAd baseAd, CTAdError adError) {
        onAdShowFailed(adError);
    }

    private void onAdShowFailed(CTAdError adError) {
        Pair<String, String> errorPair = CTToponMediation.getAdError(adError);
        if (mImpressionListener != null) {
            mImpressionListener.onRewardedVideoAdPlayFailed(errorPair.first, errorPair.second);
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mImpressionListener != null) {
            mImpressionListener.onRewardedVideoAdPlayClicked();
        }
    }

    @Override
    public void onVideoComplete(CTBaseAd baseAd) {
        if (mImpressionListener != null) {
            mImpressionListener.onRewardedVideoAdPlayEnd();
        }
    }
}
