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
import jp.co.jokerpiece.piecebase.data.DetailReserveData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.HttpClient;

import static jp.co.jokerpiece.piecebase.util.App.getContext;

/**
 * Created by Antifuture on 2016/9/29.
 */

public class DetailReserveAPI extends AsyncTaskLoader<DetailReserveData> implements HttpClient.HttpClientInterface
{
    private String app_id = Config.APP_ID;
    private String app_key = Config.APP_KEY;
    //private String uuid = Common.getUUID(getContext());
    private String cal_date ="null";

    public DetailReserveAPI(Context context, Bundle bundle)
    {
        super(context);

        cal_date = bundle.getString("cal_date");

    }

    @Override
    public DetailReserveData loadInBackground()
    {
        DetailReserveData detailReserveData = new DetailReserveData();

        String result = null;
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("app_id", app_id);
        parameter.put("app_key", app_key);
        //parameter.put("uuid", uuid);
        parameter.put("cal_date",cal_date);

        String url = Config.EVENT_LIST;

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
                detailReserveData.error_code = rootObject.getString("error_code");
                detailReserveData.error_message = rootObject.getString("error_message");
            }
            else
            {
                detailReserveData.error_code = rootObject.getString("error_code");
                detailReserveData.error_message = rootObject.getString("error_message");
            }


        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return detailReserveData;
    }

    @Override
    public void HttpClientProgress(float progress)
    {

    }
}
