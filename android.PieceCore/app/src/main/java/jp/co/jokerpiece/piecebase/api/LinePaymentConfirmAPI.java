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
import jp.co.jokerpiece.piecebase.data.LinePaymentConfirmData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.HttpClient;

/**
 * Created by Sou on 16/4/8.
 */
public class LinePaymentConfirmAPI extends AsyncTaskLoader<LinePaymentConfirmData> implements HttpClient.HttpClientInterface
{

    private String app_id = Config.APP_ID;
    private String app_key = Config.APP_KEY;
    private String uuid = Common.getUUID(getContext());
    private String transaction;
    private String amount;
    private String order_id;
    private String currency="JPY";

    public LinePaymentConfirmAPI(Context context, Bundle bundle)
    {
        super(context);

        transaction = bundle.getString("trans_no");
        amount = bundle.getString("payment_price");
        order_id = bundle.getString("order_id");

    }

    @Override
    public LinePaymentConfirmData loadInBackground()
    {

        String result = null;
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("app_id", app_id);
        //parameter.put("app_key", app_key);
        parameter.put("uuid", uuid);
        parameter.put("transaction", transaction);
        parameter.put("amount", amount);
        parameter.put("order_id", order_id);
        parameter.put("currency",currency);

        String url = Config.ITEM_DETAIL;

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
            if(error_code != 0)
            {
                return null;
            }


        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void HttpClientProgress(float progress)
    {

    }
}