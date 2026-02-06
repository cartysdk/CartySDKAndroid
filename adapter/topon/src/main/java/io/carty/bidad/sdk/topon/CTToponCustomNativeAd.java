package io.carty.bidad.sdk.topon;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.secmtp.sdk.nativead.api.ATNativePrepareInfo;
import com.secmtp.sdk.nativead.unitgroup.api.CustomNativeAd;

import java.util.List;

import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.interfaces.ICTNativeInfo;
import io.carty.bidad.sdk.openapi.nativead.CTNative;
import io.carty.bidad.sdk.openapi.nativead.CTNativeAdListener;

public class CTToponCustomNativeAd extends CustomNativeAd implements CTNativeAdListener {

    private CTNative mCTNative;
    private CTBaseAd mBaseAd;
    private Context mContext;

    public CTToponCustomNativeAd(Context context, CTNative ctNative, CTBaseAd baseAd) {
        this.mContext = context;
        this.mCTNative = ctNative;
        this.mBaseAd = baseAd;
        setAdData();
    }

    private void setAdData() {
        if (mBaseAd == null || mBaseAd.getNativeInfo() == null) {
            return;
        }

        ICTNativeInfo nativeInfo = mBaseAd.getNativeInfo();
        setTitle(nativeInfo.getTitle());
        setDescriptionText(nativeInfo.getSubTitle());
        setAdChoiceIconUrl(nativeInfo.getAdChoiceUrl());
        setIconImageUrl(nativeInfo.getIconUrl());
        setMainImageUrl(nativeInfo.getImageUrl());
        double rating = 0d;
        if (!TextUtils.isEmpty(nativeInfo.getRating())) {
            try {
                rating = Double.parseDouble(nativeInfo.getRating());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        setStarRating(rating);
        setCallToActionText(nativeInfo.getCallToAction());
        setAdvertiserName(nativeInfo.getSponsored());
    }

    @Override
    public View getAdMediaView(Object... object) {
        Log.i(CTToponMediation.TAG, "native getMediaView");
        if (mCTNative != null && mContext != null) {
            return mCTNative.getMediaView(mContext);
        }
        return super.getAdMediaView(object);
    }

    @Override
    public void prepare(View view, ATNativePrepareInfo nativePrepareInfo) {
        Log.i(CTToponMediation.TAG, "native prepare");
        List<View> clickViewList = nativePrepareInfo.getClickViewList();
        View container = nativePrepareInfo.getParentView();
        if (container instanceof ViewGroup) {
            if (mCTNative != null) {
                mCTNative.registerViewForInteraction((ViewGroup) container, clickViewList, this);
            }
        }
    }

    @Override
    public void destroy() {
        if (mCTNative != null) {
            mCTNative.destroy();
        }
        mContext = null;
    }

    @Override
    public boolean isNativeExpress() {
        return false;
    }

    @Override
    public void onShown(CTBaseAd baseAd) {
        notifyAdImpression();
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        notifyAdClicked();
    }

    @Override
    public void onVideoPlay(CTBaseAd baseAd) {
        notifyAdVideoStart();
    }

    @Override
    public void onVideoComplete(CTBaseAd baseAd) {
        notifyAdVideoEnd();
    }
}
