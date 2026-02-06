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

    @Override
    public void loadCustomAd(Context context, Map<String, Object> userParams, Map<String, String> tpParams) {
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
        mBannerAdImpl = new TPBannerAdImpl(null, mBannerView);
        if (mLoadAdapterListener != null) {
            mLoadAdapterListener.loadAdapterLoaded(mBannerAdImpl);
        }
    }

    @Override
    public void onLoadFailed(CTAdError adError) {
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
