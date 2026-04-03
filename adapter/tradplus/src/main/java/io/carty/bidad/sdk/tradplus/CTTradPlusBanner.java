package io.carty.bidad.sdk.tradplus;

import android.content.Context;
import android.util.Log;

import com.tradplus.ads.base.adapter.banner.TPBannerAdImpl;
import com.tradplus.ads.base.adapter.banner.TPBannerAdapter;

import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.banner.CTBannerAdListener;
import io.carty.bidad.sdk.openapi.banner.CTBannerView;

public class CTTradPlusBanner extends TPBannerAdapter implements CTBannerAdListener {

    private TPBannerAdImpl mBannerAdImpl;
    private CTBannerView mBannerView;
    private OnC2STokenListener mOnC2STokenListener;
    private boolean mC2SBidding, mC2SBiddingLoaded;

    @Override
    public void getC2SBidding(Context context, Map<String, Object> userParams, Map<String, String> tpParams, OnC2STokenListener onC2STokenListener) {
        Log.i(CTTradPlusMediation.TAG, "banner getC2SBidding");
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
        Log.i(CTTradPlusMediation.TAG, "loadBanner");
        CTTradPlusMediation.init(context, tpParams, new CTAdSdk.CTInitListener() {
            @Override
            public void onInitSuccess() {
                startLoadAd(context, userParams, tpParams);
            }

            @Override
            public void onInitFailed(CTAdError adError) {
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoadFailed(CTTradPlusMediation.getAdError(adError));
                }
            }
        });
    }

    private void startLoadAd(Context context, Map<String, Object> userParams, Map<String, String> tpParams) {
        CTAdRequest.AdSize adSize;
        int width = CTTradPlusMediation.getIntFromStringMap(tpParams, CTTradPlusMediation.KEY_BANNER_WIDTH);
        int height = CTTradPlusMediation.getIntFromStringMap(tpParams, CTTradPlusMediation.KEY_BANNER_HEIGHT);
        if (width == 0 || height == 0) {
            width = CTTradPlusMediation.getIntFromMap(userParams, CTTradPlusMediation.KEY_BANNER_WIDTH);
            height = CTTradPlusMediation.getIntFromMap(userParams, CTTradPlusMediation.KEY_BANNER_HEIGHT);
        }
        if (width == 320 && height == 100) {
            adSize = CTAdRequest.AdSize.BANNER_320_100;
        } else if (width == 300 && height == 250) {
            adSize = CTAdRequest.AdSize.BANNER_300_250;
        } else {
            adSize = CTAdRequest.AdSize.BANNER_320_50;
        }
        CTAdRequest.Builder builder = CTTradPlusMediation.getAdRequest(tpParams);
        builder.setAdSize(adSize);
        mBannerView = new CTBannerView(context, builder.build());
        mBannerView.setBannerAdListener(this);
        mBannerView.loadAd();
    }

    @Override
    public void setLossNotifications(String auctionPrice, String auctionPriceCny, String lossReason) {
        Log.i(CTTradPlusMediation.TAG, "setLossNotifications auctionPrice:" + auctionPrice
                + " auctionPriceCny:" + auctionPriceCny + " lossReason:" + lossReason);
        if (mBannerView != null) {
            mBannerView.onC2SBiddingFailed(auctionPrice, null);
        }
    }

    @Override
    public void clean() {
        if (mBannerView != null) {
            mBannerView.destroy();
        }
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
        mBannerAdImpl = new TPBannerAdImpl(null, mBannerView);
        if (mLoadAdapterListener != null) {
            mLoadAdapterListener.loadAdapterLoaded(mBannerAdImpl);
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
        if (mBannerAdImpl != null) {
            mBannerAdImpl.adClosed();
        }
    }

    @Override
    public void onShown(CTBaseAd baseAd) {
        if (mBannerAdImpl != null) {
            mBannerAdImpl.adShown();
        }
        if (mBannerView != null) {
            String secondPrice = CTTradPlusMediation.getSecondPrice(getWaterfallBean());
            Log.i(CTTradPlusMediation.TAG, "setWinNotifications secondPrice:" + secondPrice);
            mBannerView.onC2SBiddingSuccess(secondPrice, null);
        }
    }

    @Override
    public void onShowFailed(CTBaseAd baseAd, CTAdError adError) {
        if (mBannerAdImpl != null) {
            mBannerAdImpl.onAdShowFailed(CTTradPlusMediation.getAdError(adError));
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mBannerAdImpl != null) {
            mBannerAdImpl.adClicked();
        }
    }
}
