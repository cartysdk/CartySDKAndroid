package io.carty.bidad.sdk.tradplus;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.tradplus.ads.base.adapter.nativead.TPNativeAdView;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.base.network.response.ConfigResponse;

import java.util.ArrayList;
import java.util.List;

import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.interfaces.ICTNativeInfo;
import io.carty.bidad.sdk.openapi.nativead.CTNative;
import io.carty.bidad.sdk.openapi.nativead.CTNativeAdListener;

public class CTTradPlusCustomNativeAd extends TPBaseAd implements CTNativeAdListener {

    private Context mContext;
    private CTNative mCTNative;
    private CTBaseAd mBaseAd;
    private View mExpressView;
    private ConfigResponse.WaterfallBean mWaterFallBean;

    public CTTradPlusCustomNativeAd(Context context, CTNative ctNative, CTBaseAd baseAd, ConfigResponse.WaterfallBean waterfallBean) {
        this.mContext = context;
        this.mCTNative = ctNative;
        this.mBaseAd = baseAd;
        this.mWaterFallBean = waterfallBean;
        if (mCTNative != null) {
            mExpressView = mCTNative.getNativeAdView(context, this);
        }
    }

    @Override
    public Object getNetworkObj() {
        return null;
    }

    @Override
    public void registerClickView(ViewGroup viewGroup, ArrayList<View> arrayList) {
        Log.i(CTTradPlusMediation.TAG, "native registerClickView");
        if (getNativeAdType() == AD_TYPE_NATIVE_EXPRESS) {
            Log.i(CTTradPlusMediation.TAG, "native express do not registerViewForInteraction");
            return;
        }
        if (mCTNative != null) {
            mCTNative.registerViewForInteraction(viewGroup, arrayList, this);
        }
    }

    @Override
    public TPNativeAdView getTPNativeView() {
        Log.i(CTTradPlusMediation.TAG, "native getTPNativeView");
        TPNativeAdView nativeAdView = new TPNativeAdView();
        if (mBaseAd != null && mBaseAd.getNativeInfo() != null) {
            ICTNativeInfo nativeInfo = mBaseAd.getNativeInfo();
            nativeAdView.setTitle(nativeInfo.getTitle());
            nativeAdView.setSubTitle(nativeInfo.getSubTitle());
            nativeAdView.setCallToAction(nativeInfo.getCallToAction());
            nativeAdView.setIconImageUrl(nativeInfo.getIconUrl());
            nativeAdView.setMainImageUrl(nativeInfo.getImageUrl());
            nativeAdView.setAdChoiceUrl(nativeInfo.getAdChoiceUrl());
            double rating = 0d;
            if (!TextUtils.isEmpty(nativeInfo.getRating())) {
                try {
                    rating = Double.parseDouble(nativeInfo.getRating());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            nativeAdView.setStarRating(rating);
            nativeAdView.setSponsoredLabel(nativeInfo.getSponsored());
            if (mCTNative != null) {
                View mediaView = mCTNative.getMediaView(mContext);
                if (mediaView != null) {
                    nativeAdView.setMediaView(mediaView);
                }
            }
        }
        return nativeAdView;
    }

    @Override
    public int getNativeAdType() {
        return (mExpressView == null ? AD_TYPE_NORMAL_NATIVE : AD_TYPE_NATIVE_EXPRESS);
    }

    @Override
    public View getRenderView() {
        return mExpressView;
    }

    @Override
    public List<View> getMediaViews() {
        return null;
    }

    @Override
    public ViewGroup getCustomAdContainer() {
        return null;
    }

    @Override
    public void clean() {
        if (mCTNative != null) {
            mCTNative.destroy();
        }
        mContext = null;
        mWaterFallBean = null;
    }

    @Override
    public void onShown(CTBaseAd baseAd) {
        if (mShowListener != null) {
            mShowListener.onAdShown();
        }
        if (mCTNative != null) {
            String secondPrice = CTTradPlusMediation.getSecondPrice(mWaterFallBean);
            Log.i(CTTradPlusMediation.TAG, "setWinNotifications secondPrice:" + secondPrice);
            mCTNative.onC2SBiddingSuccess(secondPrice, null);
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mShowListener != null) {
            mShowListener.onAdClicked();
        }
    }
}
