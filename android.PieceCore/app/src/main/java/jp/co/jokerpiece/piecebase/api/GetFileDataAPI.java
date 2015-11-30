package jp.co.jokerpiece.piecebase.api;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.GetFileData;
import jp.co.jokerpiece.piecebase.util.HttpClient;

/**
 * Created by wenHsin on 2015/11/19.
 */
public class GetFileDataAPI extends AsyncTaskLoader<GetFileData> implements HttpClient.HttpClientInterface{
    String order_id;
    public GetFileDataAPI(Context context,String order_id) {
        super(context);
        this.order_id = order_id;
    }

    @Override
    public GetFileData loadInBackground() {

        GetFileData getFileData = new GetFileData();
        HashMap<String,String> parameter = new HashMap<String,String>();
        parameter.put("app_id", Config.APP_ID);
        parameter.put("order_id",order_id);
        parameter.put("os_type","1");

        String result = null;
        try{
            byte[] resData = HttpClient.getByteArrayFromUrlPost(Config.SENDID_GETFILEDATA,parameter,this);
            if(resData == null) return null;
            result = new String(resData,"UTF-8");
 //           Log.d("playback",result);
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            e.printStackTrace();
        }

        try{
            JSONObject rootObject = new JSONObject(result);

            getFileData.status_code = rootObject.getString("status_code");
            if(!rootObject.isNull("error_message")) {
                getFileData.error_msg = rootObject.getString("error_message");
            }
            getFileData.type_code = rootObject.getString("type_code");
            getFileData.file_data = rootObject.getString("file_data");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getFileData;
    }

    @Override
    public void HttpClientProgress(float progress) {

    }
}
