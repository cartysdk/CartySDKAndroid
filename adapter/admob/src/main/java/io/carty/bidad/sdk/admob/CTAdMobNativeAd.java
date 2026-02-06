package io.carty.bidad.sdk.admob;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationNativeAdCallback;
import com.google.android.gms.ads.mediation.MediationNativeAdConfiguration;
import com.google.android.gms.ads.mediation.NativeAdMapper;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdAssetNames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.interfaces.ICTNativeInfo;
import io.carty.bidad.sdk.openapi.nativead.CTNative;
import io.carty.bidad.sdk.openapi.nativead.CTNativeAdListener;
import io.carty.bidad.sdk.openapi.nativead.CTNativeLoadListener;

public class CTAdMobNativeAd extends NativeAdMapper implements CTNativeLoadListener, CTNativeAdListener {

    private Context mContext;
    private CTNative mCTNative;
    private MediationAdLoadCallback<NativeAdMapper, MediationNativeAdCallback> mCallback;
    private MediationNativeAdCallback mNativeAdCallback;

    public void loadAd(@NonNull MediationNativeAdConfiguration mediationNativeAdConfiguration,
                       @NonNull MediationAdLoadCallback<NativeAdMapper, MediationNativeAdCallback> mediationAdLoadCallback) {
        this.mCallback = mediationAdLoadCallback;
        this.mContext = mediationNativeAdConfiguration.getContext();
        mCTNative = new CTNative(CTAdMobAdapter.getAdRequestBuilder(
                mediationNativeAdConfiguration.getServerParameters()).build(), this);
        mCTNative.loadAd();
    }

    @Override
    public void onLoaded(CTBaseAd baseAd) {
        mNativeAdCallback = mCallback.onSuccess(this);
        mapNativeAd(baseAd);
    }

    private void mapNativeAd(CTBaseAd baseAd) {
        if (baseAd != null && baseAd.getNativeInfo() != null) {
            ICTNativeInfo nativeInfo = baseAd.getNativeInfo();
            String title = nativeInfo.getTitle();
            if (!TextUtils.isEmpty(title)) {
                setHeadline(title);
            }
            String subTitle = nativeInfo.getSubTitle();
            if (!TextUtils.isEmpty(subTitle)) {
                setBody(subTitle);
            }
            String rating = nativeInfo.getRating();
            if (!TextUtils.isEmpty(rating)) {
                Double ratingD = null;
                try {
                    ratingD = Double.valueOf(rating);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (ratingD != null) {
                    setStarRating(ratingD);
                }
            }
            String sponsored = nativeInfo.getSponsored();
            if (!TextUtils.isEmpty(sponsored)) {
                setAdvertiser(sponsored);
            }
            String callToAction = nativeInfo.getCallToAction();
            if (!TextUtils.isEmpty(callToAction)) {
                setCallToAction(callToAction);
            }
            String iconUrl = nativeInfo.getIconUrl();
            if (!TextUtils.isEmpty(iconUrl)) {
                setIcon(new CTMappedImage(iconUrl));
            }
            View mediaView = mCTNative.getMediaView(mContext);
            if (mediaView != null) {
                setMediaView(mediaView);
            }
            setOverrideClickHandling(true);
        }
    }

    @Override
    public void onLoadFailed(CTAdError adError) {
        mCallback.onFailure(CTAdMobAdapter.getAdError(adError));
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mNativeAdCallback != null) {
            mNativeAdCallback.reportAdClicked();
        }
    }

    @Override
    public void onVideoPlay(CTBaseAd baseAd) {
        if (mNativeAdCallback != null) {
            mNativeAdCallback.onVideoPlay();
        }
    }

    @Override
    public void onVideoPause(CTBaseAd baseAd) {
        if (mNativeAdCallback != null) {
            mNativeAdCallback.onVideoPause();
        }
    }

    @Override
    public void onVideoComplete(CTBaseAd baseAd) {
        if (mNativeAdCallback != null) {
            mNativeAdCallback.onVideoComplete();
        }
    }

    @Override
    public void onVideoMute(CTBaseAd baseAd) {
        if (mNativeAdCallback != null) {
            mNativeAdCallback.onVideoMute();
        }
    }

    @Override
    public void onVideoUnmute(CTBaseAd baseAd) {
        if (mNativeAdCallback != null) {
            mNativeAdCallback.onVideoUnmute();
        }
    }

    @Override
    public void trackViews(@NonNull View view, @NonNull Map<String, View> clickableAssetViews, @NonNull Map<String, View> map1) {
        Log.i(CTAdMobAdapter.TAG, "native trackViews");
        HashMap<String, View> copyClickableAssetViews = new HashMap<>(clickableAssetViews);
        copyClickableAssetViews.remove(NativeAdAssetNames.ASSET_ADCHOICES_CONTAINER_VIEW);
        // adChoicesView
        copyClickableAssetViews.remove("3012");
        ArrayList<View> clickableViewList = new ArrayList<>(copyClickableAssetViews.values());
        if (mCTNative != null && view instanceof ViewGroup) {
            mCTNative.registerViewForInteraction((ViewGroup) view, clickableViewList, this);
        }
    }

    public static class CTMappedImage extends NativeAd.Image {
        private final Uri mUri;

        public CTMappedImage(@NonNull String url) {
            mUri = Uri.parse(url);
        }

        @Override
        public double getScale() {
            return 1d;
        }

        @Nullable
        @Override
        public Drawable getDrawable() {
            return null;
        }

        @Nullable
        @Override
        public Uri getUri() {
            return mUri;
        }
    }
}
