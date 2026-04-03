package io.carty.bidad.sdk.tradplus;

import android.content.Context;
import android.util.Log;

import com.tradplus.ads.base.adapter.reward.TPRewardAdapter;

import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.reward.CTReward;
import io.carty.bidad.sdk.openapi.reward.CTRewardAdListener;

public class CTTradPlusReward extends TPRewardAdapter implements CTRewardAdListener {

    private CTReward mCTReward;
    private OnC2STokenListener mOnC2STokenListener;
    private boolean mC2SBidding, mC2SBiddingLoaded;

    @Override
    public void getC2SBidding(Context context, Map<String, Object> userParams, Map<String, String> tpParams, OnC2STokenListener onC2STokenListener) {
        Log.i(CTTradPlusMediation.TAG, "reward getC2SBidding");
        this.mC2SBidding = true;
        this.mC2SBiddingLoaded = false;
        this.mOnC2STokenListener = onC2STokenListener;
        loadCustomAd(context, userParams, tpParams);
    }

    @Override
    public void loadCustomAd(Context context, Map<String, Object> userParams, Map<String, String> tpParams) {
        if (mC2SBidding && mC2SBiddingLoaded) {
            onAdLoaded();
            return;
        }
        Log.i(CTTradPlusMediation.TAG, "loadReward");
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
        mCTReward = new CTReward(builder.build());
        mCTReward.setRewardAdListener(this);
        mCTReward.loadAd();
    }

    @Override
    public void setLossNotifications(String auctionPrice, String auctionPriceCny, String lossReason) {
        Log.i(CTTradPlusMediation.TAG, "setLossNotifications auctionPrice:" + auctionPrice
                + " auctionPriceCny:" + auctionPriceCny + " lossReason:" + lossReason);
        if (mCTReward != null) {
            mCTReward.onC2SBiddingFailed(auctionPrice, null);
        }
    }

    @Override
    public void showAd() {
        Log.i(CTTradPlusMediation.TAG, "reward showAd");
        if (mCTReward != null) {
            mCTReward.showAd(null);
        }
    }

    @Override
    public boolean isReady() {
        if (mCTReward != null) {
            return mCTReward.isReady();
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
        if (mC2SBidding) {
            mC2SBiddingLoaded = true;
            CTTradPlusMediation.onC2SBiddingSuccess(baseAd, mOnC2STokenListener);
        } else {
            onAdLoaded();
        }
    }

    private void onAdLoaded() {
        if (mLoadAdapterListener != null) {
            mLoadAdapterListener.loadAdapterLoaded(null);
        }
    }

    @Override
    public void onLoadFailed(CTAdError adError) {
        if (mC2SBidding) {
            CTTradPlusMediation.onC2SBiddingFailed(adError, mOnC2STokenListener);
        } else {
            onAdLoadFailed(adError);
        }
    }

    private void onAdLoadFailed(CTAdError adError) {
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
        if (mCTReward != null) {
            String secondPrice = CTTradPlusMediation.getSecondPrice(getWaterfallBean());
            Log.i(CTTradPlusMediation.TAG, "setWinNotifications secondPrice:" + secondPrice);
            mCTReward.onC2SBiddingSuccess(secondPrice, null);
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mShowListener != null) {
            mShowListener.onAdClicked();
        }
    }

    @Override
    public void onRewarded(CTBaseAd baseAd, Map<String, Object> rewardMap) {
        if (mShowListener != null) {
            mShowListener.onReward(rewardMap);
        }
    }

    @Override
    public void onVideoPlay(CTBaseAd baseAd) {
        if (mShowListener != null) {
            mShowListener.onAdVideoStart();
        }
    }

    @Override
    public void onVideoComplete(CTBaseAd baseAd) {
        if (mShowListener != null) {
            mShowListener.onAdVideoEnd();
        }
    }
}
