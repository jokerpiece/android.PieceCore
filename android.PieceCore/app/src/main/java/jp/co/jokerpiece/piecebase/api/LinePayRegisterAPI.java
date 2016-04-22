package jp.co.jokerpiece.piecebase.api;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;

import jp.co.jokerpiece.piecebase.data.LinePayRegisterData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.HttpClient;

/**
 * Created by Sou on 16/4/6.
 */
public class LinePayRegisterAPI extends AsyncTaskLoader<LinePayRegisterData> implements HttpClient.HttpClientInterface {


    private String productName;
    private String productImageUrl;
    private String amount;
    private String confirmUrl=Config.URL_SCHEME_LINEPAY_ACTIVITY;
    private String orderId;
    private String currency ="JPY";



    public LinePayRegisterAPI(Context context, Bundle bundle)
    {
        super(context);

        productName=bundle.getString("item_title");
        productImageUrl=bundle.getString("img_url");
        orderId=bundle.getString("order_id");
        amount = bundle.getString("payment_price");

    }

    @Override
    public LinePayRegisterData loadInBackground()
    {


        String result = null;
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("app_id", Config.APP_ID);
        parameter.put("app_key", Config.APP_KEY);
        parameter.put("uuid", Common.getUUID(getContext()));
        parameter.put("productName", productName);
        parameter.put("productImageUrl", productImageUrl);
        parameter.put("amount", amount);
        parameter.put("confirmUrl", confirmUrl);
        parameter.put("orderId",orderId);
        parameter.put("currency",currency);

        LinePayRegisterData linePayRegisterData = new LinePayRegisterData();
        String url = Config.LINE_PAY_REGISTER;

        try
        {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(url, parameter,this);
            if(resData == null)
            {
                return null;
            }

            result = new String(resData, "UTF-8");

            AppUtil.debugLog("RESULT", result);
            AppUtil.debugLog("app_id", Config.APP_ID);
            AppUtil.debugLog("app_key",Config.APP_KEY);
            AppUtil.debugLog("uuid",Common.getUUID(getContext()));
            AppUtil.debugLog("productName",productName);
            AppUtil.debugLog("productImageUrl",productImageUrl);
            AppUtil.debugLog("amount", amount);
            AppUtil.debugLog("confirmUrl", confirmUrl);
            AppUtil.debugLog("currency", currency);
            AppUtil.debugLog("orderId", orderId);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return null;
        }

        //JSONDATA
        try
        {
            JSONObject rootObject = new JSONObject(result);
            AppUtil.debugLog("JSON", rootObject.toString());

            int error_code = rootObject.getInt("error_code");
            linePayRegisterData.returnCode = rootObject.getInt("error_code");
            if(error_code != 0)
            {
                linePayRegisterData.returnMessage=rootObject.getString("returnMessage");
                return linePayRegisterData;

            }
            else


            linePayRegisterData.paymentUrlWeb=rootObject.getString("paymentUrl.web");
            linePayRegisterData.paymentUrl=rootObject.getString("paymentUrl.app");
            linePayRegisterData.transaction=rootObject.getString("transaction");
            AppUtil.debugLog("JSON_paymentUrlWeb", linePayRegisterData.paymentUrlWeb);
            AppUtil.debugLog("JSON_paymentUrl", linePayRegisterData.paymentUrl);
            AppUtil.debugLog("JSON_transaction", linePayRegisterData.transaction);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return linePayRegisterData;
    }

    @Override
    public void HttpClientProgress(float progress)
    {

    }
}
