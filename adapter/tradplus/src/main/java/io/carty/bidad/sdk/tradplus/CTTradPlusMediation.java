package io.carty.bidad.sdk.tradplus;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tradplus.ads.base.adapter.TPBaseAdapter;
import com.tradplus.ads.base.common.TPError;
import com.tradplus.ads.base.network.response.ConfigResponse;
import com.tradplus.ads.open.TradPlusSdk;

import java.util.HashMap;
import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdConfig;
import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdRequest;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.CTGlobalSettings;

public class CTTradPlusMediation {
    public static final String TAG = "CTTradPlusMediation";
    private static final String MEDIATION = "TradPlus";
    private static final String NETWORK_NAME = "CartySdk";
    private static final String KEY_APP_ID = "appid";
    public static final String KEY_PLACEMENT_ID = "pid";
    public static final String KEY_BANNER_WIDTH = "carty_banner_width";
    public static final String KEY_BANNER_HEIGHT = "carty_banner_height";

    private static String sAppId;


    private CTTradPlusMediation() {
    }

    public static void init(Context context, Map<String, String> tpParams, CTAdSdk.CTInitListener listener) {
        if (TextUtils.isEmpty(sAppId)) {
            CTGlobalSettings.getInstance().setCoppa(TradPlusSdk.getGDPRChild(context));
            CTGlobalSettings.getInstance().setGdpr(TradPlusSdk.getGDPRDataCollection(context) == 0);
            CTGlobalSettings.getInstance().setMediation(MEDIATION);
            if (tpParams != null && !tpParams.isEmpty()) {
                sAppId = tpParams.get(KEY_APP_ID);
            }
            Log.i(TAG, "init appId:" + sAppId);
        }
        CTAdConfig config = new CTAdConfig.Builder().setAppId(sAppId).build();
        CTAdSdk.init(context, config, listener);
    }


    public static String getNetworkVersion() {
        return CTAdSdk.getSdkVersionName();
    }

    public static String getNetworkName() {
        return NETWORK_NAME;
    }

    public static CTAdRequest.Builder getAdRequest(Map<String, String> tpParams) {
        CTAdRequest.Builder builder = new CTAdRequest.Builder();
        if (tpParams != null && !tpParams.isEmpty()) {
            String placementId = tpParams.get(KEY_PLACEMENT_ID);
            Log.i(TAG, "placementId:" + placementId);
            builder.setPlacementId(placementId);
        }
        return builder;
    }

    public static TPError getAdError(CTAdError adError) {
        String code = "";
        String msg = "";
        if (adError != null) {
            code = String.valueOf(adError.getErrorCode());
            msg = String.valueOf(adError.getErrorMsg());
        }

        return new TPError(code, msg);
    }

    public static int getIntFromStringMap(Map<String, String> map, String key) {
        try {
            if (!TextUtils.isEmpty(key) && map != null && !map.isEmpty()) {
                String str = map.get(key);
                if (!TextUtils.isEmpty(str)) {
                    return Integer.parseInt(str);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getIntFromMap(Map<String, Object> map, String key) {
        try {
            if (!TextUtils.isEmpty(key) && map != null && !map.isEmpty()) {
                Object obj = map.get(key);
                if (obj instanceof Integer) {
                    return (int) obj;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void onC2SBiddingSuccess(CTBaseAd ctBaseAd, TPBaseAdapter.OnC2STokenListener listener) {
        if (ctBaseAd != null && listener != null) {
            Map<String, Object> map = new HashMap<>();
            // key "ecpm" value double
            double ecpm = ctBaseAd.getEcpm();
            Log.i(TAG, "onC2SBiddingSuccess ecpm:" + ecpm);
            map.put("ecpm", ecpm);
            listener.onC2SBiddingResult(map);
        }
    }

    public static void onC2SBiddingFailed(CTAdError error, TPBaseAdapter.OnC2STokenListener listener) {
        if (listener != null) {
            String errorMsg = "";
            if (error != null) {
                errorMsg = "code:" + error.getErrorCode() + " msg:" + error.getErrorMsg();
                Log.i(TAG, "onC2SBiddingFailed errorMsg:" + errorMsg);
                listener.onC2SBiddingFailed("", errorMsg);
            }
        }
    }

    public static String getSecondPrice(ConfigResponse.WaterfallBean waterfallBean) {
        try {
            return waterfallBean.getPayLoadInfo().getSecondPrice();
        } catch (Throwable e) {
            return "";
        }
    }
}
