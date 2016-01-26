package jp.co.jokerpiece.piecebase.api;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentHistory;
import com.paypal.base.ConfigManager;
import com.paypal.base.Constants;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.base.rest.PayPalResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jp.co.jokerpiece.piecebase.data.PaypalData;
import jp.co.jokerpiece.piecebase.data.RegustAppPaymentData;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.HttpClient.HttpClientInterface;

public class PayPalAPI extends AsyncTaskLoader<PaypalData> implements HttpClientInterface {


    String order_id = null;
    double latitude;
    double longitude;
    int type;
    RegustAppPaymentData regustAppPaymentData = new RegustAppPaymentData();
    public static String aToken;

    public PayPalAPI(Context context) {
        super(context);

    }

    @Override
    public PaypalData loadInBackground() {

        OAuthTokenCredential oAuthTokenCredential;
        Payment paymenResoult;
        PaymentHistory paymenHis;

        String acient = "AdkYj_7K_NKg-ygjEV7vb6bLITUtVPkh3PfUtBBN8-IM02AOO-k2uU1osCF99ei1UBOLhIVemU5cD--m";
        String bsercret = "EG2mcU7xa9rzZv7pgw-BrkhRE-fWBbxnHrhjGcTF-A92iYSxyGGK4mH64bE-l8ofEJ31LUc84W_WXqvd";
        String payid = "PAY-82P19035N60866837K2MKZRI";

        try {
            InputStream is = App.getContext().getResources().getAssets().open("sdk_config.properties");

            oAuthTokenCredential = PayPalResource.initConfig(is);

            aToken = oAuthTokenCredential.getAccessToken();

            paymenResoult = Payment.get(aToken, payid);

            paymenResoult = paymenResoult;


        } catch (IOException e) {
            e.printStackTrace();

        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }




        return null;
    }


    @Override
    public void HttpClientProgress(float progress) {

    }

}
