package io.carty.bidad.sdk.tradplus;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.tradplus.ads.base.adapter.splash.TPSplashAdapter;

import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.splash.CTSplash;
import io.carty.bidad.sdk.openapi.splash.CTSplashAdListener;

public class CTTradPlusSplash extends TPSplashAdapter implements CTSplashAdListener {

    private CTSplash mCTSplash;
    private Context mContext;
    private OnC2STokenListener mOnC2STokenListener;
    private boolean mC2SBidding, mC2SBiddingLoaded;

    @Override
    public void getC2SBidding(Context context, Map<String, Object> userParams, Map<String, String> tpParams, OnC2STokenListener onC2STokenListener) {
        Log.i(CTTradPlusMediation.TAG, "splash getC2SBidding");
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
        Log.i(CTTradPlusMediation.TAG, "load splashAd");
        this.mContext = context;
        CTTradPlusMediation.init(context, tpParams, new CTAdSdk.CTInitListener() {
            @Override
            public void onInitSuccess() {
                Log.i(CTTradPlusMediation.TAG, "init success");
                startLoadAd(tpParams);
            }

            @Override
            public void onInitFailed(CTAdError adError) {
                Log.i(CTTradPlusMediation.TAG, "init failed");
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoadFailed(CTTradPlusMediation.getAdError(adError));
                }
            }
        });
    }

    private void startLoadAd(Map<String, String> tpParams) {
        CTAdRequest.Builder builder = CTTradPlusMediation.getAdRequest(tpParams);
        mCTSplash = new CTSplash(builder.build());
        mCTSplash.setSplashAdListener(this);
        mCTSplash.loadAd();
    }

    @Override
    public void setLossNotifications(String auctionPrice, String auctionPriceCny, String lossReason) {
        Log.i(CTTradPlusMediation.TAG, "setLossNotifications auctionPrice:" + auctionPrice
                + " auctionPriceCny:" + auctionPriceCny + " lossReason:" + lossReason);
        if (mCTSplash != null) {
            mCTSplash.onC2SBiddingFailed(auctionPrice, null);
        }
    }

    @Override
    public void showAd() {
        Log.i(CTTradPlusMediation.TAG, "splash showAd");
        Activity activity = null;
        if (mContext instanceof Activity) {
            activity = (Activity) mContext;
        }
        if (mCTSplash != null) {
            mCTSplash.showAd(activity);
        }
    }

    @Override
    public void clean() {
    }

    @Override
    public boolean isReady() {
        if (mCTSplash != null) {
            return mCTSplash.isReady();
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
        if (mCTSplash != null) {
            String secondPrice = CTTradPlusMediation.getSecondPrice(getWaterfallBean());
            Log.i(CTTradPlusMediation.TAG, "setWinNotifications secondPrice:" + secondPrice);
            mCTSplash.onC2SBiddingSuccess(secondPrice, null);
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mShowListener != null) {
            mShowListener.onAdClicked();
        }
    }
}
