package io.carty.bidad.sdk.topon;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.secmtp.sdk.core.api.ATAdConst;
import com.secmtp.sdk.core.api.ATBiddingListener;
import com.secmtp.sdk.core.api.ATBiddingNotice;
import com.secmtp.sdk.core.api.ATBiddingResult;
import com.secmtp.sdk.core.api.ATInitMediation;
import com.secmtp.sdk.core.api.BaseAd;

import java.util.Map;
import java.util.UUID;

import io.carty.bidad.sdk.openapi.CTAdConfig;
import io.carty.bidad.sdk.openapi.CTAdError;
import io.carty.bidad.sdk.openapi.CTAdSdk;
import io.carty.bidad.sdk.openapi.CTBaseAd;
import io.carty.bidad.sdk.openapi.CTGlobalSettings;
import io.carty.bidad.sdk.openapi.interfaces.ICTBidding;

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

    public static void onC2SBiddingSuccess(ICTBidding ctBidding, CTBaseAd ctBaseAd, ATBiddingListener listener, BaseAd baseAd) {
        if (ctBaseAd != null && listener != null) {
            double ecpm = ctBaseAd.getEcpm();
            Log.i(TAG, "onC2SBiddingSuccess ecpm:" + ecpm);
            ATBiddingNotice notice = new ATBiddingNotice() {
                @Override
                public void notifyBidWin(double costPrice, double secondPrice, Map<String, Object> extra) {
                    Log.i(TAG, "notifyBidWin costPrice:" + costPrice + " secondPrice:"
                            + secondPrice + " extra:" + extra);
                    if (ctBidding != null) {
                        ctBidding.onC2SBiddingSuccess(String.valueOf(secondPrice), extra);
                    }
                }

                @Override
                public void notifyBidLoss(String lossCode, double winPrice, Map<String, Object> extra) {
                    Log.i(TAG, "notifyBidLoss lossCode:" + lossCode + " winPrice:"
                            + winPrice + " extra:" + extra);
                    if (ctBidding != null) {
                        ctBidding.onC2SBiddingFailed(String.valueOf(winPrice), extra);
                    }
                }

                @Override
                public void notifyBidDisplay(boolean isWinner, double displayPrice) {
                }

                @Override
                public ATAdConst.CURRENCY getNoticePriceCurrency() {
                    return ATAdConst.CURRENCY.USD;
                }
            };
            listener.onC2SBiddingResultWithCache(ATBiddingResult.success(ecpm, UUID.randomUUID().toString(), notice), baseAd);
        }
    }

    public static void onC2SBiddingFailed(CTAdError error, ATBiddingListener listener) {
        if (listener != null) {
            String errorMsg = "";
            if (error != null) {
                errorMsg = "code:" + error.getErrorCode() + " msg:" + error.getErrorMsg();
                Log.i(TAG, "onC2SBiddingFailed errorMsg:" + errorMsg);
                listener.onC2SBiddingResultWithCache(ATBiddingResult.fail(errorMsg), null);
            }
        }
    }
}
