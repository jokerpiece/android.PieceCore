package jp.co.jokerpiece.piecebase.api;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.LinePayProfileFragment;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.DeliveryPriceData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.HttpClient;

/**
 * Created by Antifuture on 16/4/5.
 */
public class DeliveryPriceAPI extends AsyncTaskLoader<DeliveryPriceData> implements HttpClient.HttpClientInterface
{
    private String result;
    private String post;
    private String address_tdfk;
    private String deliveryPrice;

    public DeliveryPriceAPI(Context context, Bundle bundle)
    {
        super(context);
        post = bundle.getString("Post");
        address_tdfk = bundle.getString("Address_tdfk");
    }

    @Override
    public DeliveryPriceData loadInBackground()
    {
        HashMap<String, String> parameter = new HashMap<String, String>();

        parameter.put("app_id", Config.APP_ID);
        parameter.put("app_key",Config.APP_KEY);
        parameter.put("user_id", "sample");
        parameter.put("passward", "sample");
        parameter.put("post", post);
        parameter.put("address_tdfk", address_tdfk);
        parameter.put("price", "5200");

        String url = Config.SENDID_DELIVERY_PRICE;//sending address


        //Send data to deliveryPrice Server
        try
        {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(url, parameter,this);
            if(resData == null)
            {
                return null;
            }

            result = new String(resData, "UTF-8");
            AppUtil.debugLog("RESULT", result);
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

            deliveryPrice = rootObject.getString("delivery_price");
            AppUtil.debugLog("Delivery Price", deliveryPrice);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        //saving delivery_price to DeliveryPriceData
        DeliveryPriceData deliveryPriceData = new DeliveryPriceData();
        deliveryPriceData.delivery_price = deliveryPrice;

        return deliveryPriceData;
    }

    @Override
    public void HttpClientProgress(float progress)
    {

    }


}
