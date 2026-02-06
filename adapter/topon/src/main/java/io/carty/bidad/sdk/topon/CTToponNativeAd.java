package io.carty.bidad.sdk.topon;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.secmtp.sdk.core.api.ATInitMediation;
import com.secmtp.sdk.nativead.unitgroup.api.CustomNativeAdapter;

import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.CTGlobalSettings;
import io.carty.bidad.sdk.openapi.nativead.CTNative;
import io.carty.bidad.sdk.openapi.nativead.CTNativeLoadListener;

public class CTToponNativeAd extends CustomNativeAdapter implements CTNativeLoadListener {
    private String mUnitId;
    private CTNative mCTNative;
    private Context mContext;

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        CTGlobalSettings.getInstance().setGdpr(isConsent);
        return true;
    }

    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serviceExtras, Map<String, Object> localExtras) {
        this.mContext = context;
        mUnitId = ATInitMediation.getStringFromMap(serviceExtras, CTToponMediation.KEY_UNIT_ID);
        Log.i(CTToponMediation.TAG, "loadNative mUnitId:" + mUnitId);
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
        mCTNative = new CTNative(adRequest, this);
        mCTNative.loadAd();
    }

    @Override
    public void destory() {
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
        if (mLoadListener != null) {
            mLoadListener.onAdCacheLoaded(new CTToponCustomNativeAd(mContext, mCTNative, baseAd));
        }
    }

    @Override
    public void onLoadFailed(CTAdError adError) {
        onAdLoadFailed(adError);
    }

    private void onAdLoadFailed(CTAdError adError) {
        Pair<String, String> errorPair = CTToponMediation.getAdError(adError);
        if (mLoadListener != null) {
            mLoadListener.onAdLoadError(errorPair.first, errorPair.second);
        }
    }
}
