package io.carty.bidad.sdk.tradplus;

import android.content.Context;
import android.util.Log;

import com.tradplus.ads.base.adapter.nativead.TPNativeAdapter;

import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.nativead.CTNative;
import io.carty.bidad.sdk.openapi.nativead.CTNativeLoadListener;

public class CTTradPlusNative extends TPNativeAdapter implements CTNativeLoadListener {

    private CTNative mCTNative;
    private Context mContext;
    private OnC2STokenListener mOnC2STokenListener;
    private boolean mC2SBidding, mC2SBiddingLoaded;
    private CTBaseAd mBaseAd;

    @Override
    public void getC2SBidding(Context context, Map<String, Object> userParams, Map<String, String> tpParams, OnC2STokenListener onC2STokenListener) {
        Log.i(CTTradPlusMediation.TAG, "native getC2SBidding");
        this.mC2SBidding = true;
        this.mC2SBiddingLoaded = false;
        this.mOnC2STokenListener = onC2STokenListener;
        loadCustomAd(context, userParams, tpParams);
    }

    @Override
    public void loadCustomAd(Context context, Map<String, Object> userParams, Map<String, String> tpParams) {
        if (mC2SBidding && mC2SBiddingLoaded) {
            onAdLoaded(mBaseAd);
            return;
        }
        Log.i(CTTradPlusMediation.TAG, "loadNative");
        this.mContext = context;
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
        mCTNative = new CTNative(builder.build(), this);
        mCTNative.loadAd();
    }

    @Override
    public void setLossNotifications(String auctionPrice, String auctionPriceCny, String lossReason) {
        Log.i(CTTradPlusMediation.TAG, "setLossNotifications auctionPrice:" + auctionPrice
                + " auctionPriceCny:" + auctionPriceCny + " lossReason:" + lossReason);
        if (mCTNative != null) {
            mCTNative.onC2SBiddingFailed(auctionPrice, null);
        }
    }

    @Override
    public void clean() {
        mContext = null;
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
            mBaseAd = baseAd;
            CTTradPlusMediation.onC2SBiddingSuccess(baseAd, mOnC2STokenListener);
        } else {
            onAdLoaded(baseAd);
        }
    }

    private void onAdLoaded(CTBaseAd baseAd) {
        if (mLoadAdapterListener != null) {
            mLoadAdapterListener.loadAdapterLoaded(new CTTradPlusCustomNativeAd(mContext, mCTNative, baseAd, getWaterfallBean()));
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
}
