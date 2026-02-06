package io.carty.bidad.sdk.topon;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.secmtp.sdk.banner.unitgroup.api.CustomBannerAdapter;
import com.secmtp.sdk.core.api.ATAdConst;
import com.secmtp.sdk.core.api.ATInitMediation;

import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.CTGlobalSettings;
import io.carty.bidad.sdk.openapi.banner.CTBannerAdListener;
import io.carty.bidad.sdk.openapi.banner.CTBannerView;

public class CTToponBannerAd extends CustomBannerAdapter implements CTBannerAdListener {

    private String mUnitId;
    private CTBannerView mCTBannerView;

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        CTGlobalSettings.getInstance().setGdpr(isConsent);
        return true;
    }

    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serviceExtras, Map<String, Object> localExtras) {
        mUnitId = ATInitMediation.getStringFromMap(serviceExtras, CTToponMediation.KEY_UNIT_ID);
        Log.i(CTToponMediation.TAG, "loadBanner mUnitId:" + mUnitId);
        CTToponMediation.init(context, serviceExtras, new CTAdSdk.CTInitListener() {
            @Override
            public void onInitSuccess() {
                loadAd(context, serviceExtras, localExtras);
            }

            @Override
            public void onInitFailed(CTAdError adError) {
                onAdLoadFailed(adError);
            }
        });
    }

    private void loadAd(Context context, Map<String, Object> serviceExtras, Map<String, Object> localExtras) {
        CTAdRequest.AdSize adSize;

        String size = ATInitMediation.getStringFromMap(serviceExtras, "size");
        Log.i(CTToponMediation.TAG, "banner size:" + size);
        if ("300x250".equals(size)) {
            adSize = CTAdRequest.AdSize.BANNER_300_250;
        } else if ("320x100".equals(size)) {
            adSize = CTAdRequest.AdSize.BANNER_320_100;
        } else if ("320x50".equals(size)) {
            adSize = CTAdRequest.AdSize.BANNER_320_50;
        } else {
            int localExtraWidth = ATInitMediation.getIntFromMap(localExtras, ATAdConst.KEY.AD_WIDTH);
            int localExtraHeight = ATInitMediation.getIntFromMap(localExtras, ATAdConst.KEY.AD_HEIGHT);
            Log.i(CTToponMediation.TAG, "banner localExtraWidth:" + localExtraWidth + " localExtraHeight:" + localExtraHeight);
            if (localExtraWidth > 0 && localExtraHeight > 0) {
                if (localExtraWidth == 320 && localExtraHeight == 100) {
                    adSize = CTAdRequest.AdSize.BANNER_320_100;
                } else if (localExtraWidth == 300 && localExtraHeight == 250) {
                    adSize = CTAdRequest.AdSize.BANNER_300_250;
                } else {
                    adSize = CTAdRequest.AdSize.BANNER_320_50;
                }
            } else {
                adSize = CTAdRequest.AdSize.BANNER_320_50;
            }
        }

        Log.i(CTToponMediation.TAG, "banner adRequest width:" + adSize.getWidth() + " height:" + adSize.getHeight());
        CTAdRequest adRequest = new CTAdRequest.Builder()
                .setPlacementId(getNetworkPlacementId())
                .setAdSize(adSize)
                .build();
        mCTBannerView = new CTBannerView(context, adRequest);
        mCTBannerView.setBannerAdListener(this);
        mCTBannerView.loadAd();
    }

    @Override
    public View getBannerView() {
        Log.i(CTToponMediation.TAG, "getBannerView");
        return mCTBannerView;
    }

    @Override
    public void destory() {
        if (mCTBannerView != null) {
            mCTBannerView.destroy();
        }
        mLoadListener = null;
        mImpressionEventListener = null;
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
            mLoadListener.onAdCacheLoaded();
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

    @Override
    public void onClosed(CTBaseAd baseAd) {
        if (mImpressionEventListener != null) {
            mImpressionEventListener.onBannerAdClose();
        }
    }

    @Override
    public void onShown(CTBaseAd baseAd) {
        if (mImpressionEventListener != null) {
            mImpressionEventListener.onBannerAdShow();
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mImpressionEventListener != null) {
            mImpressionEventListener.onBannerAdClicked();
        }
    }
}
