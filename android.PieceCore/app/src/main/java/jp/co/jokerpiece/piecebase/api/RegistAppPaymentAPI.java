package jp.co.jokerpiece.piecebase.api;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.RegustAppPaymentData;
import jp.co.jokerpiece.piecebase.util.HttpClient;
import jp.co.jokerpiece.piecebase.util.HttpClient.HttpClientInterface;

/*
*アプリ内決済情報登録API
 */
public class RegistAppPaymentAPI extends AsyncTaskLoader<RegustAppPaymentData> implements HttpClientInterface {

    String order_id = null;
    double latitude;
    double longitude;
    int type;
    RegustAppPaymentData regustAppPaymentData = new RegustAppPaymentData();
    String aToken;
    public RegistAppPaymentAPI(Context context, RegustAppPaymentData paymentData) {
        super(context);

        this.regustAppPaymentData = paymentData;
    }

    @Override
    public RegustAppPaymentData loadInBackground() {
        RegustAppPaymentData data = null;
        //APIに渡すパラメータをHashMapに詰める。
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("app_id", regustAppPaymentData.app_id);
        parameter.put("order_no", regustAppPaymentData.order_no);
        parameter.put("app_key", regustAppPaymentData.app_key);
        parameter.put("product_id", regustAppPaymentData.product_id);
        parameter.put("payment_price", regustAppPaymentData.payment_price);
        parameter.put("trans_no", regustAppPaymentData.trans_no);
        parameter.put("item_price", regustAppPaymentData.item_price);
        parameter.put("amount", regustAppPaymentData.amount.toString());
        parameter.put("fee", regustAppPaymentData.fee.toString());
        parameter.put("payment_kbn", regustAppPaymentData.payment_kbn);
        parameter.put("user_id", regustAppPaymentData.user_id);
        parameter.put("mail_address", regustAppPaymentData.mail_address);
        parameter.put("sei", regustAppPaymentData.sei);
        parameter.put("mei", regustAppPaymentData.mei);
        parameter.put("post", regustAppPaymentData.post);
        parameter.put("address_tdfk", regustAppPaymentData.address_tdfk);
        parameter.put("address_city", regustAppPaymentData.address_city);
        parameter.put("address_street", regustAppPaymentData.address_street);
        parameter.put("tel", regustAppPaymentData.tel);

        String result = null;
        try {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(Config.SENDID_REGUST_APP_PAYMENT, parameter, this);
            if (resData == null) {
                return null;
            }
            result = new String(resData, "UTF-8");
            //AppUtil.debugLog("RESULT",result);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        try {
            data = new RegustAppPaymentData();

            JSONObject rootObject = new JSONObject(result);
            //AppUtil.debugLog("JSON", rootObject.toString());
            int error_code = rootObject.getInt("error_code");

            data.error_code = error_code;
            data.error_messsege = rootObject.getString("error_message");

            if (error_code != 0) {
                return data;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void HttpClientProgress(float progress) {

    }


    static public class LocationData {
        public double lat;
        public double lng;
        public String updated;
        public String error_msg;
    }
}
