package io.carty.bidad.sdk.tradplus;

import android.content.Context;
import android.util.Log;

import com.tradplus.ads.base.adapter.interstitial.TPInterstitialAdapter;

import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.interstitial.CTInterstitial;
import io.carty.bidad.sdk.openapi.interstitial.CTInterstitialAdListener;

public class CTTradPlusInterstitial extends TPInterstitialAdapter implements CTInterstitialAdListener {

    private CTInterstitial mCTInterstitial;
    private OnC2STokenListener mOnC2STokenListener;
    private boolean mC2SBidding, mC2SBiddingLoaded;

    @Override
    public void getC2SBidding(Context context, Map<String, Object> userParams, Map<String, String> tpParams, OnC2STokenListener onC2STokenListener) {
        Log.i(CTTradPlusMediation.TAG, "interstitial getC2SBidding");
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
        Log.i(CTTradPlusMediation.TAG, "loadInterstitial");
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
        mCTInterstitial = new CTInterstitial(builder.build());
        mCTInterstitial.setInterstitialAdListener(this);
        mCTInterstitial.loadAd();
    }

    @Override
    public void setLossNotifications(String auctionPrice, String auctionPriceCny, String lossReason) {
        Log.i(CTTradPlusMediation.TAG, "setLossNotifications auctionPrice:" + auctionPrice
                + " auctionPriceCny:" + auctionPriceCny + " lossReason:" + lossReason);
        if (mCTInterstitial != null) {
            mCTInterstitial.onC2SBiddingFailed(auctionPrice, null);
        }
    }

    @Override
    public void showAd() {
        Log.i(CTTradPlusMediation.TAG, "interstitial showAd");
        if (mCTInterstitial != null) {
            mCTInterstitial.showAd(null);
        }
    }

    @Override
    public boolean isReady() {
        if (mCTInterstitial != null) {
            return mCTInterstitial.isReady();
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
        if (mCTInterstitial != null) {
            String secondPrice = CTTradPlusMediation.getSecondPrice(getWaterfallBean());
            Log.i(CTTradPlusMediation.TAG, "setWinNotifications secondPrice:" + secondPrice);
            mCTInterstitial.onC2SBiddingSuccess(secondPrice, null);
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mShowListener != null) {
            mShowListener.onAdClicked();
        }
    }
}
