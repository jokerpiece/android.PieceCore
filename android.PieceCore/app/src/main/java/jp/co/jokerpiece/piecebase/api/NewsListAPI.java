package jp.co.jokerpiece.piecebase.api;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.NewsListData;
import jp.co.jokerpiece.piecebase.data.NewsListData.NewsData;
import jp.co.jokerpiece.piecebase.util.HttpClient;
import jp.co.jokerpiece.piecebase.util.HttpClient.HttpClientInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

public class NewsListAPI extends AsyncTaskLoader<NewsListData> implements HttpClientInterface {
	public NewsListAPI(Context context) {
		super(context);
	}

	@Override
	public NewsListData loadInBackground() {
		NewsListData newsListData = new NewsListData();

    	HashMap<String, String> parameter = new HashMap<String, String>();
    	parameter.put("app_id", Config.APP_ID);
    	parameter.put("uuid", Common.getUUID(getContext()));
        String result = null;
        try {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(Config.SENDID_NEWS_LIST, parameter,this);
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
			JSONArray dataArray = rootObject.getJSONArray("list");
			newsListData.data_list = new ArrayList<NewsListData.NewsData>();
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject jsonObject = dataArray.getJSONObject(i);

			    NewsData data = newsListData.new NewsData();
			    data.news_id = jsonObject.getString("news_id");
			    data.title = jsonObject.getString("title");
			    //data.text = jsonObject.getString("text");
				data.type = jsonObject.getString("type");
				data.id = jsonObject.getString("id");
			    newsListData.data_list.add(data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return newsListData;
	}

	@Override
	public void HttpClientProgress(float progress) {

	}

}
