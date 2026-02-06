package io.carty.bidad.sdk.topon;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.secmtp.sdk.core.api.ATInitMediation;

import java.util.Map;

import io.carty.bidad.sdk.openapi.CTAdConfig;
import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTGlobalSettings;

public class CTToponMediation {
    public static final String TAG = "CTToponMediation";
    private static final String MEDIATION = "TopOn";
    private static final String NETWORK_NAME = "CartySdk";
    private static final String KEY_APP_ID = "appid";
    public static final String KEY_UNIT_ID = "slot_id";

    private static String sAppId;


    private CTToponMediation() {
    }

    public static void init(Context context, Map<String, Object> serviceExtras, CTAdSdk.CTInitListener listener) {
        if (TextUtils.isEmpty(sAppId)) {
            CTGlobalSettings.getInstance().setMediation(MEDIATION);
            sAppId = ATInitMediation.getStringFromMap(serviceExtras, KEY_APP_ID);
            Log.i(TAG, "init appId:" + sAppId);
        }
        CTAdConfig config = new CTAdConfig.Builder().setAppId(sAppId).build();
        CTAdSdk.init(context, config, listener);
    }


    public static String getNetworkSDKVersion() {
        return CTAdSdk.getSdkVersionName();
    }

    public static String getNetworkName() {
        return NETWORK_NAME;
    }

    public static Pair<String, String> getAdError(CTAdError adError) {
        String code = "";
        String msg = "";
        if (adError != null) {
            code = String.valueOf(adError.getErrorCode());
            msg = String.valueOf(adError.getErrorMsg());
        }

        return new Pair<>(code, msg);
    }
}
