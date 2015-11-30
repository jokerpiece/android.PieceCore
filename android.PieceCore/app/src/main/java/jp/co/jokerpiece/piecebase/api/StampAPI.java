package jp.co.jokerpiece.piecebase.api;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.StampListData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.HttpClient;

/**
 * Created by kaku on 2015/05/08.
 */
public class StampAPI extends AsyncTaskLoader<StampListData> implements HttpClient.HttpClientInterface {
    private String stampId;

    public StampAPI(Context context, String stampId) {
            super(context);
            this.stampId = stampId;
            }

    @Override
    public StampListData loadInBackground() {
        StampListData stampListData = new StampListData();

        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("app_id", Config.APP_ID);
        parameter.put("app_key", Config.APP_KEY);
        parameter.put("uuid", Common.getUUID(getContext()));

        String result = null;
        try {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(Config.SENDID_STAMP_LIST , parameter,this);
        if(resData == null){
            return null;
        }
        result = new String(resData, "UTF-8");
        AppUtil.debugLog("RESULT", result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        try {
                JSONObject rootObject = new JSONObject(result);

                if(!rootObject.isNull("more_flg")) {
                    if (rootObject.getInt("more_flg") == 1) {
                        stampListData.more_flg = true;
                    } else {
                        stampListData.more_flg = false;
                    }
                }

                stampListData.start_date = rootObject.getString("start_date");
                stampListData.end_date = rootObject.getString("end_date");
                stampListData.stamp_id = rootObject.getString("stamp_id");
                stampListData.get_point = rootObject.getString("get_point");
                stampListData.total_point = rootObject.getString("total_point");
                stampListData.title = rootObject.getString("title");
                stampListData.message = rootObject.getString("message");
                AppUtil.debugLog("test","startday:"+stampListData.start_date);
                AppUtil.debugLog("test","endday:"+stampListData.end_date);
                AppUtil.debugLog("test","point:"+stampListData.get_point);
                AppUtil.debugLog("test","stampId:"+stampListData.stamp_id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return stampListData;
        }


    @Override
    public void HttpClientProgress(float progress) {

    }


}
