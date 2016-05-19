package jp.co.jokerpiece.piecebase.api;

import android.app.Application;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.LinePayProfileFragment;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.ItemListData;
import jp.co.jokerpiece.piecebase.data.LinePayData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.HttpClient;

/**
 * Created by Sou on 16/3/31.
 */
public class SendProfileAPI extends AsyncTaskLoader<LinePayData> implements HttpClient.HttpClientInterface
{

    private String user_id="";                   //= ユーザID
    private String password="";                  //= パスワード
    private String sei;                             //= 性
    private String mei;                             //= 名
    //private String birthday="";             //= 誕生日yyyyMMdd
    private String post;                            //= 郵便番号
    private String address_tdfk;                    //= 住所(都道府県)
    private String address_city;                    //= 住所(市区町村)
    private String address_street;                  //= 住所(番地)
    //private String sex="0";                         //= 性別 0:男 1:女
    private String mail_address;                    //= メールアドレス
    private String tel;                             //= 電話番号
    //private String anniversary_name="";    //= 記念日名
    //private String anniversary="";          //= yyyyMMdd

    public SendProfileAPI(Context context, Bundle bundle)
    {
        super(context);
        sei = bundle.getString("Sei");
        mei = bundle.getString("Mei");
        post = bundle.getString("Post");
        address_tdfk = bundle.getString("Address_tdfk");
        address_city = bundle.getString("Address_city");
        address_street = bundle.getString("Address_street");
        tel = bundle.getString("Tel");
        mail_address = bundle.getString("Mail_address");

    }

    @Override
    public LinePayData loadInBackground()
    {


        //
        String result = null;
        HashMap<String, String> parameter = new HashMap<String, String>();

        parameter.put("app_id",Config.APP_ID);
        parameter.put("app_key",Config.APP_KEY);
        parameter.put("uuid", Common.getUUID(getContext()));
        parameter.put("Sei", sei);
        parameter.put("Mei", mei);
        parameter.put("Post", post);
        parameter.put("Address_tdfk", address_tdfk);
        parameter.put("Address_city", address_city);
        parameter.put("Address_street", address_street);
        parameter.put("mail_address", mail_address);
        parameter.put("Tel", tel);

        parameter.put("user_id",user_id);
        parameter.put("Password",password);
        //parameter.put("Birthday",birthday);
        //parameter.put("Sex",sex);
        //parameter.put("anniversary_name",anniversary_name);
        //parameter.put("anniversary",anniversary);


        AppUtil.debugLog("Data",Config.APP_ID+"\n"+Config.APP_KEY+"\n"+Common.getUUID(getContext())+
                "\n"+sei+"\n"+mei+"\n"+post+"\n"+address_city+"\n"+address_street+"\n"+address_tdfk+"\n"
                +mail_address+"\n"+tel+"\n");

        String url = Config.SENDID_SEND_PROFILE;//sending address


        //Send data to Profile Server
        try
        {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(url, parameter,this);
            if(resData == null)
            {
                return null;
            }

            result = new String(resData, "UTF-8");
            AppUtil.debugLog("RESULT",result);
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
