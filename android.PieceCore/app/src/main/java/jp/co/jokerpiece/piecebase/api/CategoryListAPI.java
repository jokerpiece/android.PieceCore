package jp.co.jokerpiece.piecebase.api;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.CategoryListData;
import jp.co.jokerpiece.piecebase.data.CategoryListData.CategoryData;
import jp.co.jokerpiece.piecebase.util.HttpClient;
import jp.co.jokerpiece.piecebase.util.HttpClient.HttpClientInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class CategoryListAPI extends AsyncTaskLoader<CategoryListData> implements HttpClientInterface {
	public CategoryListAPI(Context context) {
		super(context);
	}

	@Override
	public CategoryListData loadInBackground() {
		CategoryListData categoryData = new CategoryListData();

        String result = null;
        HashMap<String, String> parameter = new HashMap<String, String>();
    	parameter.put("app_id", Config.APP_ID);

        try {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(Config.SENDID_CTGRY, parameter,this);
            if(resData == null){
            	return null;
            }
            result = new String(resData, "UTF-8");
            //Log.d("RESULT",result);        	
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
			categoryData.data_list = new ArrayList<CategoryListData.CategoryData>();

			JSONArray dataArray = rootObject.getJSONArray("categoryLists");
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject jsonObject = dataArray.getJSONObject(i);

				CategoryData data = categoryData.new CategoryData();
//				data.category_id = jsonObject.getInt("category_id");
//                Object o = jsonObject.get("category_id");
//                if(o instanceof String){
//
//                }
                data.category_id = jsonObject.getString("category_id");
			    data.category_name = jsonObject.getString("title");
			    data.shop_category_url = jsonObject.getString("shop_url");
			    data.img_url = jsonObject.getString("img_url");
			    data.category_text = jsonObject.getString("text");

			    categoryData.data_list.add(data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return categoryData;
	}

	@Override
	public void HttpClientProgress(float progress) {
		
	}
}
