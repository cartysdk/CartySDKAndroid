package io.carty.bidad.sdk.max;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.adapter.MaxAdViewAdapter;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxAppOpenAdapter;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.MaxNativeAdAdapter;
import com.applovin.mediation.adapter.MaxRewardedAdapter;
import com.applovin.mediation.adapter.MaxSignalProvider;
import com.applovin.mediation.adapter.listeners.MaxAdViewAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxAppOpenAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxNativeAdAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxRewardedAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxSignalCollectionListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterSignalCollectionParameters;
import com.applovin.mediation.adapters.MediationAdapterBase;
import com.applovin.mediation.nativeAds.MaxNativeAd;
import com.applovin.sdk.AppLovinSdk;

import java.util.List;
import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdConfig;
import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.CTGlobalSettings;
import io.carty.bidad.sdk.openapi.banner.CTBannerAdListener;
import io.carty.bidad.sdk.openapi.banner.CTBannerView;
import io.carty.bidad.sdk.openapi.interfaces.ICTNativeInfo;
import io.carty.bidad.sdk.openapi.interfaces.ICTReward;
import io.carty.bidad.sdk.openapi.interstitial.CTInterstitial;
import io.carty.bidad.sdk.openapi.interstitial.CTInterstitialAdListener;
import io.carty.bidad.sdk.openapi.nativead.CTNative;
import io.carty.bidad.sdk.openapi.nativead.CTNativeAdListener;
import io.carty.bidad.sdk.openapi.nativead.CTNativeLoadListener;
import io.carty.bidad.sdk.openapi.reward.CTReward;
import io.carty.bidad.sdk.openapi.reward.CTRewardAdListener;
import io.carty.bidad.sdk.openapi.splash.CTSplash;
import io.carty.bidad.sdk.openapi.splash.CTSplashAdListener;

public class CTMaxAdapter extends MediationAdapterBase
        implements MaxSignalProvider, MaxInterstitialAdapter, MaxAppOpenAdapter, MaxRewardedAdapter, MaxAdViewAdapter, MaxNativeAdAdapter {

    private static final String TAG = "CTMaxAdapter";

    private CTSplash mCTSplash;
    private CTInterstitial mCTInterstitial;
    private CTReward mCTReward;
    private CTBannerView mCTBannerView;
    private CTNative mCTNative;

    public CTMaxAdapter(AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    public void collectSignal(MaxAdapterSignalCollectionParameters maxAdapterSignalCollectionParameters, Activity activity, MaxSignalCollectionListener maxSignalCollectionListener) {
        Log.i(TAG, "collectSignal");
        if (maxSignalCollectionListener != null) {
            maxSignalCollectionListener.onSignalCollectionFailed("not support");
        }
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters maxAdapterInitializationParameters, Activity activity, OnCompletionListener onCompletionListener) {
        Log.i(TAG, "initialize");
        CTGlobalSettings.getInstance().setMediation(CTMaxConstants.MEDIATION);
        String appId = "";
        if (maxAdapterInitializationParameters != null && maxAdapterInitializationParameters.getServerParameters() != null) {
            appId = maxAdapterInitializationParameters.getServerParameters().getString(CTMaxConstants.KEY_APP_ID);
        }
        Log.i(TAG, "appId:" + appId);
        CTAdConfig config = new CTAdConfig.Builder()
                .setAppId(appId)
                .build();
        CTAdSdk.init(getContext(activity), config, new CTAdSdk.CTInitListener() {
            @Override
            public void onInitSuccess() {
                Log.i(TAG, "onInitSuccess");
                if (onCompletionListener != null) {
                    onCompletionListener.onCompletion(InitializationStatus.INITIALIZED_SUCCESS, null);
                }
            }

            @Override
            public void onInitFailed(CTAdError adError) {
                String msg = "";
                if (adError != null) {
                    msg = "code:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg();
                }
                Log.i(TAG, "onInitFailed msg:" + msg);
                if (onCompletionListener != null) {
                    onCompletionListener.onCompletion(InitializationStatus.INITIALIZED_FAILURE, msg);
                }
            }
        });
    }


    @Override
    public String getSdkVersion() {
        return CTAdSdk.getSdkVersionName();
    }

    @Override
    public String getAdapterVersion() {
        return BuildConfig.NETWORK_VERSION_NAME;
    }

    @Override
    public void onDestroy() {
        if (mCTBannerView != null) {
            mCTBannerView.destroy();
        }
        mCTBannerView = null;

        if (mCTNative != null) {
            mCTNative.destroy();
        }
        mCTNative = null;
    }

    @Override
    public void loadAppOpenAd(@NonNull MaxAdapterResponseParameters maxAdapterResponseParameters, @Nullable Activity activity, @NonNull MaxAppOpenAdapterListener maxAppOpenAdapterListener) {
        Log.i(TAG, "loadAppOpenAd");
        CTAdRequest adRequest = getAdRequest(maxAdapterResponseParameters).build();
        mCTSplash = new CTSplash(adRequest);
        mCTSplash.setSplashAdListener(new CTSplashAdListener() {
            @Override
            public void onLoaded(CTBaseAd baseAd) {
                Bundle bundle = getLoadedBundle(baseAd);
                if (bundle == null) {
                    maxAppOpenAdapterListener.onAppOpenAdLoaded();
                } else {
                    maxAppOpenAdapterListener.onAppOpenAdLoaded(bundle);
                }
            }

            @Override
            public void onLoadFailed(CTAdError adError) {
                maxAppOpenAdapterListener.onAppOpenAdLoadFailed(getMaxAdapterError(adError));
            }

            @Override
            public void onShown(CTBaseAd baseAd) {
                maxAppOpenAdapterListener.onAppOpenAdDisplayed();
            }

            @Override
            public void onShowFailed(CTBaseAd baseAd, CTAdError adError) {
                maxAppOpenAdapterListener.onAppOpenAdDisplayFailed(getMaxAdapterError(adError));
            }

            @Override
            public void onClicked(CTBaseAd baseAd) {
                maxAppOpenAdapterListener.onAppOpenAdClicked();
            }

            @Override
            public void onClosed(CTBaseAd baseAd) {
                maxAppOpenAdapterListener.onAppOpenAdHidden();
            }
        });
        mCTSplash.loadAd();
    }

    @Override
    public void showAppOpenAd(@NonNull MaxAdapterResponseParameters maxAdapterResponseParameters, @Nullable Activity activity, @NonNull MaxAppOpenAdapterListener maxAppOpenAdapterListener) {
        Log.i(TAG, "showAppOpenAd");
        if (mCTSplash != null) {
            mCTSplash.showAd(activity);
        } else {
            maxAppOpenAdapterListener.onAppOpenAdDisplayFailed(getMaxAdapterError(new CTAdError(CTMaxConstants.DEFAULT_ERROR_CODE, "mCTSplash is null")));
        }
    }

    @Override
    public void loadInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.i(TAG, "loadInterstitialAd");
        CTAdRequest adRequest = getAdRequest(maxAdapterResponseParameters).build();
        mCTInterstitial = new CTInterstitial(adRequest);
        mCTInterstitial.setInterstitialAdListener(new CTInterstitialAdListener() {
            @Override
            public void onLoaded(CTBaseAd baseAd) {
                if (maxInterstitialAdapterListener != null) {
                    Bundle bundle = getLoadedBundle(baseAd);
                    if (bundle == null) {
                        maxInterstitialAdapterListener.onInterstitialAdLoaded();
                    } else {
                        maxInterstitialAdapterListener.onInterstitialAdLoaded(bundle);
                    }
                }
            }

            @Override
            public void onLoadFailed(CTAdError adError) {
                if (maxInterstitialAdapterListener != null) {
                    maxInterstitialAdapterListener.onInterstitialAdLoadFailed(getMaxAdapterError(adError));
                }
            }

            @Override
            public void onShown(CTBaseAd baseAd) {
                if (maxInterstitialAdapterListener != null) {
                    maxInterstitialAdapterListener.onInterstitialAdDisplayed();
                }
            }

            @Override
            public void onShowFailed(CTBaseAd baseAd, CTAdError adError) {
                if (maxInterstitialAdapterListener != null) {
                    maxInterstitialAdapterListener.onInterstitialAdDisplayFailed(getMaxAdapterError(adError));
                }
            }

            @Override
            public void onClicked(CTBaseAd baseAd) {
                if (maxInterstitialAdapterListener != null) {
                    maxInterstitialAdapterListener.onInterstitialAdClicked();
                }
            }

            @Override
            public void onClosed(CTBaseAd baseAd) {
                if (maxInterstitialAdapterListener != null) {
                    maxInterstitialAdapterListener.onInterstitialAdHidden();
                }
            }
        });
        mCTInterstitial.loadAd();
    }

    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.i(TAG, "showInterstitialAd");
        if (mCTInterstitial != null) {
            if (!mCTInterstitial.isReady()) {
                if (maxInterstitialAdapterListener != null) {
                    maxInterstitialAdapterListener.onInterstitialAdDisplayFailed(getMaxAdapterNotReadyError());
                }
                return;
            }
            mCTInterstitial.showAd(activity);
        } else {
            if (maxInterstitialAdapterListener != null) {
                maxInterstitialAdapterListener.onInterstitialAdDisplayFailed(getMaxAdapterError(new CTAdError(CTMaxConstants.DEFAULT_ERROR_CODE, "mCTSplash is null")));
            }
        }
    }

    @Override
    public void loadRewardedAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxRewardedAdapterListener maxRewardedAdapterListener) {
        Log.i(TAG, "loadRewardedAd");
        CTAdRequest adRequest = getAdRequest(maxAdapterResponseParameters).build();
        mCTReward = new CTReward(adRequest);
        mCTReward.setRewardAdListener(new CTRewardAdListener() {
            @Override
            public void onLoaded(CTBaseAd baseAd) {
                if (maxRewardedAdapterListener != null) {
                    Bundle bundle = getLoadedBundle(baseAd);
                    if (bundle == null) {
                        maxRewardedAdapterListener.onRewardedAdLoaded();
                    } else {
                        maxRewardedAdapterListener.onRewardedAdLoaded(bundle);
                    }
                }
            }

            @Override
            public void onLoadFailed(CTAdError adError) {
                if (maxRewardedAdapterListener != null) {
                    maxRewardedAdapterListener.onRewardedAdLoadFailed(getMaxAdapterError(adError));
                }
            }

            @Override
            public void onShown(CTBaseAd baseAd) {
                if (maxRewardedAdapterListener != null) {
                    maxRewardedAdapterListener.onRewardedAdDisplayed();
                }
            }

            @Override
            public void onShowFailed(CTBaseAd baseAd, CTAdError adError) {
                if (maxRewardedAdapterListener != null) {
                    maxRewardedAdapterListener.onRewardedAdDisplayFailed(getMaxAdapterError(adError));
                }
            }

            @Override
            public void onClicked(CTBaseAd baseAd) {
                if (maxRewardedAdapterListener != null) {
                    maxRewardedAdapterListener.onRewardedAdClicked();
                }
            }

            @Override
            public void onClosed(CTBaseAd baseAd) {
                if (maxRewardedAdapterListener != null) {
                    maxRewardedAdapterListener.onRewardedAdHidden();
                }
            }

            @Override
            public void onRewarded(CTBaseAd baseAd, Map<String, Object> rewardMap) {
                if (maxRewardedAdapterListener != null) {
                    Pair<String, Integer> rewardPair = getRewardPair(rewardMap);
                    String rewardedName = rewardPair.first;
                    int rewardValue = rewardPair.second;
                    MaxReward maxReward = new MaxReward() {
                        @Override
                        public String getLabel() {
                            return rewardedName;
                        }

                        @Override
                        public int getAmount() {
                            return rewardValue;
                        }
                    };
                    maxRewardedAdapterListener.onUserRewarded(maxReward);
                }
            }
        });
        mCTReward.loadAd();
    }

    private Pair<String, Integer> getRewardPair(Map<String, Object> rewardMap) {
        String rewardedName = "";
        int rewardValue = 0;
        try {
            if (rewardMap != null) {
                Object rewardNameObj = rewardMap.get(ICTReward.REWARDED_NAME);
                Object rewardValueObj = rewardMap.get(ICTReward.REWARDED_VALUE);
                if (rewardNameObj instanceof String) {
                    rewardedName = (String) rewardNameObj;
                }
                if (rewardValueObj instanceof Integer) {
                    rewardValue = (Integer) rewardValueObj;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return new Pair<>(rewardedName, rewardValue);
    }

    @Override
    public void showRewardedAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxRewardedAdapterListener maxRewardedAdapterListener) {
        Log.i(TAG, "showRewardedAd");
        if (mCTReward != null) {
            if (!mCTReward.isReady()) {
                if (maxRewardedAdapterListener != null) {
                    maxRewardedAdapterListener.onRewardedAdDisplayFailed(getMaxAdapterNotReadyError());
                }
                return;
            }
            mCTReward.showAd(activity);
        } else {
            if (maxRewardedAdapterListener != null) {
                maxRewardedAdapterListener.onRewardedAdDisplayFailed(getMaxAdapterError(new CTAdError(CTMaxConstants.DEFAULT_ERROR_CODE, "mCTSplash is null")));
            }
        }
    }

    @Override
    public void loadNativeAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxNativeAdAdapterListener maxNativeAdAdapterListener) {
        Log.i(TAG, "loadNativeAd");
        CTAdRequest adRequest = getAdRequest(maxAdapterResponseParameters).build();
        mCTNative = new CTNative(adRequest, new CTNativeLoadListener() {
            @Override
            public void onLoaded(CTBaseAd baseAd) {
                Bundle bundle = getLoadedBundle(baseAd);
                MaxNativeAd nativeAd = getMaxNativeAd(getContext(activity), mCTNative, baseAd, bundle, maxNativeAdAdapterListener);
                if (nativeAd == null) {
                    onLoadFailed(new CTAdError(CTMaxConstants.DEFAULT_ERROR_CODE, "nativeAd is null"));
                    return;
                }
                maxNativeAdAdapterListener.onNativeAdLoaded(nativeAd, bundle);
            }

            @Override
            public void onLoadFailed(CTAdError adError) {
                maxNativeAdAdapterListener.onNativeAdLoadFailed(getMaxAdapterError(adError));
            }
        });
    }

    private MaxNativeAd getMaxNativeAd(Context context, CTNative ctNative, CTBaseAd baseAd, Bundle bundle, MaxNativeAdAdapterListener maxNativeAdAdapterListener) {
        if (ctNative == null || baseAd == null || baseAd.getNativeInfo() == null) {
            return null;
        }
        ICTNativeInfo nativeInfo = baseAd.getNativeInfo();
        View mediaView = ctNative.getMediaView(context);

        Uri iconImageUri = null;
        Uri mainImageUri = null;
        try {
            iconImageUri = Uri.parse(nativeInfo.getIconUrl());
            mainImageUri = Uri.parse(nativeInfo.getImageUrl());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        MaxNativeAd.MaxNativeAdImage iconImage = new MaxNativeAd.MaxNativeAdImage(iconImageUri);
        MaxNativeAd.MaxNativeAdImage mainImage = new MaxNativeAd.MaxNativeAdImage(mainImageUri);

        float mediaContentAspectRatio = 0.5625f;

        double starRating = 0;
        String rating = nativeInfo.getRating();
        if (!TextUtils.isEmpty(rating)) {
            Double ratingD = null;
            try {
                ratingD = Double.valueOf(rating);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (ratingD != null) {
                starRating = ratingD;
            }
        }

        MaxNativeAd.Builder builder = new MaxNativeAd.Builder()
                .setAdFormat(MaxAdFormat.NATIVE)
                .setTitle(nativeInfo.getTitle())
                .setAdvertiser(nativeInfo.getSponsored())
                .setBody(nativeInfo.getSubTitle())
                .setCallToAction(nativeInfo.getCallToAction())
                .setIcon(iconImage)
                .setMediaView(mediaView)
                .setMainImage(mainImage)
                .setMediaContentAspectRatio(mediaContentAspectRatio)
                .setStarRating(starRating);

        return new MaxNativeAd(builder) {
            @Override
            public boolean prepareForInteraction(List<View> list, ViewGroup viewGroup) {
                Log.i(TAG, "prepareForInteraction");
                if (mCTNative != null) {
                    mCTNative.registerViewForInteraction(viewGroup, list, new CTNativeAdListener() {
                        @Override
                        public void onShown(CTBaseAd baseAd1) {
                            if (maxNativeAdAdapterListener != null) {
                                maxNativeAdAdapterListener.onNativeAdDisplayed(bundle);
                            }
                        }

                        @Override
                        public void onClicked(CTBaseAd baseAd1) {
                            if (maxNativeAdAdapterListener != null) {
                                maxNativeAdAdapterListener.onNativeAdClicked();
                            }
                        }
                    });
                }
                return true;
            }
        };
    }

    @Override
    public void loadAdViewAd(MaxAdapterResponseParameters maxAdapterResponseParameters, MaxAdFormat maxAdFormat, Activity activity, MaxAdViewAdapterListener maxAdViewAdapterListener) {
        Log.i(TAG, "loadAdViewAd");
        CTAdRequest.Builder builder = getAdRequest(maxAdapterResponseParameters);
        CTAdRequest.AdSize adSize = null;
        if (maxAdFormat == MaxAdFormat.BANNER) {
            adSize = CTAdRequest.AdSize.BANNER_320_50;
        } else {
            adSize = CTAdRequest.AdSize.BANNER_300_250;
        }
        builder.setAdSize(adSize);
        mCTBannerView = new CTBannerView(getContext(activity), builder.build());
        CTAdRequest.AdSize finalAdSize = adSize;
        mCTBannerView.setBannerAdListener(new CTBannerAdListener() {
            @Override
            public void onLoaded(CTBaseAd baseAd) {
                Bundle bundle = getLoadedBundle(baseAd);
                bundle.putInt("ad_width", finalAdSize.getWidth());
                bundle.putInt("ad_height", finalAdSize.getHeight());
                if (maxAdViewAdapterListener != null) {
                    maxAdViewAdapterListener.onAdViewAdLoaded(mCTBannerView, bundle);
                }
            }

            @Override
            public void onLoadFailed(CTAdError adError) {
                if (maxAdViewAdapterListener != null) {
                    maxAdViewAdapterListener.onAdViewAdLoadFailed(getMaxAdapterError(adError));
                }
            }

            @Override
            public void onShown(CTBaseAd baseAd) {
                if (maxAdViewAdapterListener != null) {
                    maxAdViewAdapterListener.onAdViewAdDisplayed();
                }
            }

            @Override
            public void onShowFailed(CTBaseAd baseAd, CTAdError adError) {
                if (maxAdViewAdapterListener != null) {
                    maxAdViewAdapterListener.onAdViewAdDisplayFailed(getMaxAdapterError(adError));
                }
            }

            @Override
            public void onClicked(CTBaseAd baseAd) {
                if (maxAdViewAdapterListener != null) {
                    maxAdViewAdapterListener.onAdViewAdClicked();
                }
            }

            @Override
            public void onClosed(CTBaseAd baseAd) {
                if (maxAdViewAdapterListener != null) {
                    maxAdViewAdapterListener.onAdViewAdHidden();
                }
            }
        });
        mCTBannerView.loadAd();
    }

    private Context getContext(Activity activity) {
        return (activity != null) ? activity.getApplicationContext() : getApplicationContext();
    }

    private CTAdRequest.Builder getAdRequest(MaxAdapterResponseParameters maxAdapterResponseParameters) {
        String placementId = "";
        if (maxAdapterResponseParameters != null) {
            placementId = maxAdapterResponseParameters.getThirdPartyAdPlacementId();
        }
        Log.i(TAG, "placementId:" + placementId);
        CTAdRequest.Builder builder = new CTAdRequest.Builder();
        builder.setPlacementId(placementId);
        return builder;
    }

    private Bundle getLoadedBundle(CTBaseAd baseAd) {
        if (baseAd != null) {
            String requestId = baseAd.getRequestId();
            if (!TextUtils.isEmpty(requestId)) {
                Bundle bundle = new Bundle();
                bundle.putString(CTMaxConstants.KEY_CREATIVE_ID, requestId);
                return bundle;
            }
        }
        return null;
    }

    private MaxAdapterError getMaxAdapterError(CTAdError adError) {
        int code = 0;
        String msg = "";
        if (adError != null) {
            code = adError.getErrorCode();
            msg = adError.getErrorMsg();
        }
        MaxAdapterError adapterError;
        switch (code) {
            case 20000:
            case 20001:
                adapterError = MaxAdapterError.INVALID_CONFIGURATION;
                break;
            case 20002:
                adapterError = MaxAdapterError.NO_FILL;
                break;
            case 20003:
                adapterError = MaxAdapterError.NO_CONNECTION;
                break;
            case 20004:
            case 20005:
            case 20007:
                adapterError = MaxAdapterError.INTERNAL_ERROR;
                break;
            case 20006:
                adapterError = MaxAdapterError.AD_EXPIRED;
                break;
            case 30001:
                adapterError = MaxAdapterError.AD_DISPLAY_FAILED;
                break;
            default:
                adapterError = MaxAdapterError.UNSPECIFIED;
                break;
        }
        return new MaxAdapterError(adapterError, code, msg);
    }

    private MaxAdapterError getMaxAdapterNotReadyError() {
        return new MaxAdapterError(MaxAdapterError.AD_DISPLAY_FAILED,
                MaxAdapterError.AD_NOT_READY.getCode(),
                MaxAdapterError.AD_NOT_READY.getMessage());
    }
}
