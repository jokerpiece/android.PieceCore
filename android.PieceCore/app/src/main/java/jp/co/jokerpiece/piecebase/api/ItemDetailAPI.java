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
import jp.co.jokerpiece.piecebase.data.ItemDetailData;
import jp.co.jokerpiece.piecebase.data.LinePaymentConfirmData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.HttpClient;

/**
 * Created by Sou on 16/4/8.
 */
public class ItemDetailAPI extends AsyncTaskLoader<ItemDetailData> implements HttpClient.HttpClientInterface
{

    private String app_id = Config.APP_ID;
    private String app_key = Config.APP_KEY;
    private String uuid = Common.getUUID(getContext());
    //private String system;
    private String category_id;
    private String item_id;

    public ItemDetailAPI(Context context, Bundle bundle)
    {
        super(context);
        category_id = bundle.getString("category_id");
        item_id = bundle.getString("item_id");
    }

    @Override
    public ItemDetailData loadInBackground()
    {
        ItemDetailData itemDetailData = new ItemDetailData();

        String result = null;
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("app_id", app_id);
        //parameter.put("app_key", app_key);
        parameter.put("uuid", uuid);
        parameter.put("category_id",category_id);
        parameter.put("item_id",item_id);


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
            AppUtil.debugLog("category_id",item_id);
            AppUtil.debugLog("item_id",item_id);

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

        //Analyze JSONDATA
        try
        {
            JSONObject rootObject = new JSONObject(result);
            AppUtil.debugLog("JSON", rootObject.toString());

            int error_code = rootObject.getInt("error_code");

            if(error_code != 0)
            {
                itemDetailData.error_code = error_code;
                itemDetailData.error_message = rootObject.getString("error_message");
                return itemDetailData;
            }
            else
            {
                itemDetailData.error_code = error_code;
                itemDetailData.error_message = rootObject.getString("error_message");
                itemDetailData.catagory_id = rootObject.getString("category_id");
                itemDetailData.quantity = rootObject.getString("quantity");
                itemDetailData.item_id = rootObject.getString("item_id");
                itemDetailData.itemDetailJSONArray = rootObject.getJSONArray("detail");

                return itemDetailData;
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
