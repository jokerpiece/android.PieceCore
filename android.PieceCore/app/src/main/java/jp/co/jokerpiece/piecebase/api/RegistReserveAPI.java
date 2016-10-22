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
import jp.co.jokerpiece.piecebase.data.RegistReserveData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.HttpClient;
import jp.co.jokerpiece.piecebase.util.PrintHttpPostContent;

/**
 * Created by Antifuture on 2016/9/29.
 */

public class RegistReserveAPI extends AsyncTaskLoader<RegistReserveData> implements HttpClient.HttpClientInterface {
    private String app_id = Config.APP_ID;
    private String app_key = Config.APP_KEY;
    //private String uuid = Common.getUUID(getContext());
    private String user_name = "";
    private String people = "";
    private String reserve_date = "";
    private String reserve_time = "";
    private String phone = "";
    private String mail_address = "";
    private String remark="";


    public RegistReserveAPI(Context context, Bundle bundle)
    {
        super(context);

        user_name = bundle.getString("user_name");
        people = bundle.getString("people");
        reserve_date = bundle.getString("reserve_date");
        reserve_time = bundle.getString("reserve_time");
        phone = bundle.getString("phone");
        mail_address = bundle.getString("mail_address");
        remark=bundle.getString("remark");

        PrintHttpPostContent printPost = new PrintHttpPostContent(Config.REGIST_RESERVE, bundle);
        printPost.printHttpPostContent();
    }

    @Override
    public RegistReserveData loadInBackground()
    {
        RegistReserveData registReserveData = new RegistReserveData();

        String result = null;
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("app_id", app_id);
        parameter.put("app_key", app_key);
        //parameter.put("uuid", uuid);
        parameter.put("user_name",user_name);
        parameter.put("people",people);
        parameter.put("reserve_date",reserve_date);
        parameter.put("reserve_time",reserve_time);
        parameter.put("phone",phone);
        parameter.put("mail_address",mail_address);
        parameter.put("remark",remark);


        String url = Config.REGIST_RESERVE;

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

            if(rootObject.has("error_code"))
            {
                registReserveData.error_code = rootObject.getString("error_code");
            }
            else
            {
                registReserveData.error_code = "-1";
            }

            if(rootObject.has("error_message"))
            {
                registReserveData.error_message = rootObject.getString("error_message");
            }
            else
            {
                registReserveData.error_message = "";
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return registReserveData;
    }

    @Override
    public void HttpClientProgress(float progress)
    {

    }
}
