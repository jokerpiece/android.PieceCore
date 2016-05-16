package jp.co.jokerpiece.piecebase.api;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.OrderIdData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.HttpClient;

/**
 * Created by Sou on 16/4/7.
 */
public class OrderIdAPI extends AsyncTaskLoader<OrderIdData> implements HttpClient.HttpClientInterface{

    private String app_id= Config.APP_ID;
    private String app_key=Config.APP_KEY;

    public OrderIdAPI(Context context) {
        super(context);
    }

    @Override
    public OrderIdData loadInBackground()
    {

        String result = null;
        HashMap<String, String> parameter = new HashMap<String, String>();

        parameter.put("app_id",app_id);
        //parameter.put("app_key",app_key);

        String url = Config.GET_ORDER_ID;//sending address

        //Save order id to orderIdData
        OrderIdData orderIdData = new OrderIdData();
        //Get Order ID from server
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
                orderIdData.order_id = "";
                orderIdData.error_code= rootObject.getString("error_code");
                orderIdData.error_message=rootObject.getString("error_message");
                return orderIdData;
            }

            orderIdData.order_id = rootObject.getString("order_no");
            AppUtil.debugLog("orderIdData.order_id",orderIdData.order_id);



        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return orderIdData;
    }

    @Override
    public void HttpClientProgress(float progress) {

    }
}
