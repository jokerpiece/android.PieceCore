package jp.co.jokerpiece.piecebase.api;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.NewsInfoData;
import jp.co.jokerpiece.piecebase.util.HttpClient;
import jp.co.jokerpiece.piecebase.util.HttpClient.HttpClientInterface;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

public class NewsInfoAPI extends AsyncTaskLoader<NewsInfoData> implements HttpClientInterface {
	private String newsId;

	public NewsInfoAPI(Context context, String newsId) {
		super(context);
		this.newsId = newsId;
	}

	@Override
	public NewsInfoData loadInBackground() {
		NewsInfoData newsInfoData = new NewsInfoData();

    	HashMap<String, String> parameter = new HashMap<String, String>();
    	parameter.put("app_id", Config.APP_ID);
    	parameter.put("uuid", Common.getUUID(getContext()));
    	parameter.put("news_id", newsId);
        String result = null;
        try {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(Config.SENDID_NEWS_INFO, parameter,this);
            if(resData == null){
            	return null;
            }
            result = new String(resData, "UTF-8");
            Log.d("RESULT",result);
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
        try {
			JSONObject rootObject = new JSONObject(result);
			//Log.d("JSON", rootObject.toString());
			int error_code = rootObject.getInt("error_code");
			if(error_code != 0){
				return null;
			}
			newsInfoData.type = rootObject.getString("type");
			newsInfoData.newsId = rootObject.getString("news_id");
			newsInfoData.title = rootObject.getString("title");
			newsInfoData.text = rootObject.getString("text");
			//Log.d("newsInfoData", newsInfoData.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return newsInfoData;
	}

	@Override
	public void HttpClientProgress(float progress) {

	}

}
