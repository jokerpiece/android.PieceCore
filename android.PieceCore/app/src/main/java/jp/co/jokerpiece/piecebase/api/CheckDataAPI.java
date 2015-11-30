package jp.co.jokerpiece.piecebase.api;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.CheckData;
import jp.co.jokerpiece.piecebase.util.HttpClient;

/**
 * Created by wenHsin on 2015/11/13.
 */
public class CheckDataAPI extends AsyncTaskLoader<CheckData> implements HttpClient.HttpClientInterface {
    String mailAddress;
    String orderNum;
    public CheckDataAPI(Context context,String MailAddress,String OrderNum) {
        super(context);
        this.mailAddress = MailAddress;
        this.orderNum = OrderNum;
    }

    @Override
    public CheckData loadInBackground() {
        CheckData checkData = new CheckData();
        String result = null;
        HashMap<String,String> parameter = new HashMap<String, String>();
        parameter.put("uuid", Common.getUUID(getContext()));
//        parameter.put("uuid", "2321");
        parameter.put("order_num",orderNum);
        parameter.put("mail_address", mailAddress);
        parameter.put("app_id", Config.APP_ID);

//        Log.d("checkData", "APImail"+mailAddress);

        try{
             byte[] resData = HttpClient.getByteArrayFromUrlPost(Config.SENDID_CHECKDATA,parameter,this);
            if(resData == null) return null;
            result = new String(resData,"UTF-8");
           // Log.d("checkdata",result);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        try{
            JSONObject rootObject = new JSONObject(result);
            if(!rootObject.isNull("status_code")) {
                checkData.status_code = rootObject.getString("status_code");
            }
            if(!rootObject.isNull("error_message")) {
                checkData.error_msg = rootObject.getString("error_message");
            }
            checkData.order_id = rootObject.getString("order_id");
            checkData.type_code = rootObject.getString("type_code");
            checkData.token = rootObject.getString("token");
            checkData.upload_token = rootObject.getString("upload_token");
            checkData.account_id = rootObject.getString("account_id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return checkData;
    }

    @Override
    public void HttpClientProgress(float progress) {

    }
}
