package io.carty.bidad.sdk.admob;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.VersionInfo;
import com.google.android.gms.ads.mediation.InitializationCompleteCallback;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationAppOpenAd;
import com.google.android.gms.ads.mediation.MediationAppOpenAdCallback;
import com.google.android.gms.ads.mediation.MediationAppOpenAdConfiguration;
import com.google.android.gms.ads.mediation.MediationBannerAd;
import com.google.android.gms.ads.mediation.MediationBannerAdCallback;
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationInterstitialAd;
import com.google.android.gms.ads.mediation.MediationInterstitialAdCallback;
import com.google.android.gms.ads.mediation.MediationInterstitialAdConfiguration;
import com.google.android.gms.ads.mediation.MediationNativeAdCallback;
import com.google.android.gms.ads.mediation.MediationNativeAdConfiguration;
import com.google.android.gms.ads.mediation.MediationRewardedAd;
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;
import com.google.android.gms.ads.mediation.NativeAdMapper;
import com.google.android.gms.ads.mediation.rtb.RtbAdapter;
import com.google.android.gms.ads.mediation.rtb.RtbSignalData;
import com.google.android.gms.ads.mediation.rtb.SignalCallbacks;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;

import io.carty.bidad.sdk.openapi.CTAdConfig;
import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTGlobalSettings;

public class CTAdMobAdapter extends RtbAdapter {
    public static final String TAG = "CTAdMobAdapter";

    @Override
    public void initialize(@NonNull Context context, @NonNull InitializationCompleteCallback initializationCompleteCallback,
                           @NonNull List<MediationConfiguration> list) {
        Log.i(TAG, "initialize");
        HashSet<String> appIds = new HashSet<>();
        for (MediationConfiguration mediationConfiguration : list) {
            Bundle serverParameters = mediationConfiguration.getServerParameters();
            String jsonString = serverParameters.getString("parameter");
            if (!TextUtils.isEmpty(jsonString)) {
                try {
                    JSONObject json = new JSONObject(jsonString);
                    String appId = json.optString(CTAdMobConstants.KEY_APP_ID);
                    if (!TextUtils.isEmpty(appId)) {
                        Log.i(TAG, "appId:" + appId);
                        appIds.add(appId);
                    }
                } catch (Throwable e) {
                    Log.e(TAG, "parse config error: " + jsonString);
                }
            }
        }

        if (appIds.isEmpty()) {
            String msg = "appId is empty";
            Log.i(TAG, "init failed " + msg);
            initializationCompleteCallback.onInitializationFailed(msg);
            return;
        }

        CTGlobalSettings.getInstance().setMediation(CTAdMobConstants.MEDIATION);

        String appId = appIds.iterator().next();

        CTAdConfig config = new CTAdConfig.Builder()
                .setAppId(appId)
                .build();
        CTAdSdk.init(context, config, new CTAdSdk.CTInitListener() {
            @Override
            public void onInitSuccess() {
                Log.i(TAG, "onInitSuccess");
                initializationCompleteCallback.onInitializationSucceeded();
            }

            @Override
            public void onInitFailed(CTAdError adError) {
                String msg = "";
                if (adError != null) {
                    msg = "code:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg();
                }
                Log.i(TAG, "onInitFailed msg:" + msg);
                initializationCompleteCallback.onInitializationFailed(msg);
            }
        });
    }

    @Override
    public void loadAppOpenAd(@NonNull MediationAppOpenAdConfiguration mediationAppOpenAdConfiguration,
                              @NonNull MediationAdLoadCallback<MediationAppOpenAd, MediationAppOpenAdCallback> callback) {
        Log.i(TAG, "loadAppOpenAd");
        new CTAdMobAppOpenAd().loadAd(mediationAppOpenAdConfiguration, callback);
    }

    @Override
    public void loadBannerAd(@NonNull MediationBannerAdConfiguration mediationBannerAdConfiguration,
                             @NonNull MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> callback) {
        Log.i(TAG, "loadBannerAd");
        new CTAdMobBannerAd().loadAd(mediationBannerAdConfiguration, callback);
    }

    @Override
    public void loadInterstitialAd(@NonNull MediationInterstitialAdConfiguration mediationInterstitialAdConfiguration,
                                   @NonNull MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback> callback) {
        Log.i(TAG, "loadInterstitialAd");
        new CTAdMobInterstitialAd().loadAd(mediationInterstitialAdConfiguration, callback);
    }

    @Override
    public void loadNativeAdMapper(@NonNull MediationNativeAdConfiguration mediationNativeAdConfiguration,
                                   @NonNull MediationAdLoadCallback<NativeAdMapper, MediationNativeAdCallback> mediationAdLoadCallback) throws RemoteException {
        Log.i(TAG, "loadNativeAdMapper");
        new CTAdMobNativeAd().loadAd(mediationNativeAdConfiguration, mediationAdLoadCallback);
    }

    @Override
    public void loadRewardedAd(@NonNull MediationRewardedAdConfiguration mediationRewardedAdConfiguration,
                               @NonNull MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> callback) {
        Log.i(TAG, "loadRewardedAd");
        new CTAdMobRewardAd().loadAd(mediationRewardedAdConfiguration, callback);
    }

    @Override
    public void loadRewardedInterstitialAd(@NonNull MediationRewardedAdConfiguration mediationRewardedAdConfiguration,
                                           @NonNull MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> callback) {
        Log.i(TAG, "loadRewardedInterstitialAd");
        loadRewardedAd(mediationRewardedAdConfiguration, callback);
    }

    @Override
    public void collectSignals(@NonNull RtbSignalData rtbSignalData,
                               @NonNull SignalCallbacks signalCallbacks) {
        Log.i(TAG, "collectSignals");
        signalCallbacks.onFailure(getRtbAdError());
    }

    @Override
    public void loadRtbAppOpenAd(@NonNull MediationAppOpenAdConfiguration adConfiguration,
                                 @NonNull MediationAdLoadCallback<MediationAppOpenAd, MediationAppOpenAdCallback> callback) {
        Log.i(TAG, "loadRtbAppOpenAd");
        callback.onFailure(getRtbAdError());
    }

    @Override
    public void loadRtbBannerAd(@NonNull MediationBannerAdConfiguration adConfiguration,
                                @NonNull MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> callback) {
        Log.i(TAG, "loadRtbBannerAd");
        callback.onFailure(getRtbAdError());
    }

    @Override
    public void loadRtbInterstitialAd(@NonNull MediationInterstitialAdConfiguration adConfiguration,
                                      @NonNull MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback> callback) {
        Log.i(TAG, "loadRtbInterstitialAd");
        callback.onFailure(getRtbAdError());
    }

    @Override
    public void loadRtbNativeAdMapper(@NonNull MediationNativeAdConfiguration adConfiguration,
                                      @NonNull MediationAdLoadCallback<NativeAdMapper, MediationNativeAdCallback> callback) throws RemoteException {
        Log.i(TAG, "loadRtbNativeAdMapper");
        callback.onFailure(getRtbAdError());
    }

    @Override
    public void loadRtbRewardedAd(@NonNull MediationRewardedAdConfiguration adConfiguration,
                                  @NonNull MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> callback) {
        Log.i(TAG, "loadRtbRewardedAd");
        callback.onFailure(getRtbAdError());
    }

    @Override
    public void loadRtbRewardedInterstitialAd(@NonNull MediationRewardedAdConfiguration adConfiguration,
                                              @NonNull MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> callback) {
        Log.i(TAG, "loadRtbRewardedInterstitialAd");
        loadRtbRewardedAd(adConfiguration, callback);
    }

    private AdError getRtbAdError() {
        return new AdError(CTAdMobConstants.DEFAULT_ERROR_CODE,
                "not support rtb", CTAdMobConstants.ERROR_DOMAIN);
    }

    @NonNull
    @Override
    public VersionInfo getSDKVersionInfo() {
        String versionString = CTAdSdk.getSdkVersionName();
        String[] splits = versionString.split("\\.");

        if (splits.length >= 3) {
            int major = Integer.parseInt(splits[0]);
            int minor = Integer.parseInt(splits[1]);
            int micro = Integer.parseInt(splits[2]);
            return new VersionInfo(major, minor, micro);
        }

        return new VersionInfo(0, 0, 0);
    }

    @NonNull
    @Override
    public VersionInfo getVersionInfo() {
        String versionString = BuildConfig.NETWORK_VERSION_NAME;
        String[] splits = versionString.split("\\.");
        if (splits.length >= 4) {
            int major = Integer.parseInt(splits[0]);
            int minor = Integer.parseInt(splits[1]);
            int micro = Integer.parseInt(splits[2]) * 100 + Integer.parseInt(splits[3]);
            return new VersionInfo(major, minor, micro);
        }
        return new VersionInfo(0, 0, 0);
    }

    public static CTAdRequest.Builder getAdRequestBuilder(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        String placementId = "";
        String jsonString = bundle.getString("parameter");
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                placementId = jsonObject.getString(CTAdMobConstants.KEY_PLACEMENT_ID);
                Log.e(TAG, "placementId:" + placementId);
            } catch (Throwable e) {
                Log.e(TAG, "placementId parse error: " + jsonString);
            }
        }
        return new CTAdRequest.Builder()
                .setPlacementId(placementId);
    }

    public static AdError getAdError(CTAdError adError) {
        int code = CTAdMobConstants.DEFAULT_ERROR_CODE;
        String msg = "";
        if (adError != null) {
            code = adError.getErrorCode();
            msg = adError.getErrorMsg();
        }
        return new AdError(code, msg, CTAdMobConstants.CARTY_SDK_DOMAIN);
    }
}
