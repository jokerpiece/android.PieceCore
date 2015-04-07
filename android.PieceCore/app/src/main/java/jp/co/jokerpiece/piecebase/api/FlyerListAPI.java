package jp.co.jokerpiece.piecebase.api;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.FlyerData;
import jp.co.jokerpiece.piecebase.data.FlyerData.FlyerBodyData;
import jp.co.jokerpiece.piecebase.data.FlyerData.FlyerHeaderData;
import jp.co.jokerpiece.piecebase.util.HttpClient;
import jp.co.jokerpiece.piecebase.util.HttpClient.HttpClientInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class FlyerListAPI extends AsyncTaskLoader<FlyerData> implements HttpClientInterface {
	int flyerID = -1;
	public FlyerListAPI(Context context,int flyerID) {
		super(context);
		this.flyerID = flyerID;
	}

	@Override
	public FlyerData loadInBackground() {
		if(flyerID < 0){
			return null;
		}
		FlyerData flyerData = new FlyerData();

    	HashMap<String, String> parameter = new HashMap<String, String>();
    	parameter.put("app_id", Config.APP_ID);
    	parameter.put("flyer_id", String.valueOf(flyerID));
        String result = null;
        try {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(Config.SENDID_FLIYER_LIST, parameter,this);
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
			flyerData.header_list = new ArrayList<FlyerData.FlyerHeaderData>();
			flyerData.body_list = new ArrayList<FlyerData.FlyerBodyData>();

			JSONArray dataHeaderArray = rootObject.getJSONArray("headFlyer");
			for (int i = 0; i < dataHeaderArray.length(); i++) {
				JSONObject jsonObject = dataHeaderArray.getJSONObject(i);

				FlyerHeaderData data = flyerData.new FlyerHeaderData();
			    data.img_url = jsonObject.getString("img_url");
			    if(!jsonObject.isNull("category_id")){
			    	data.category_id = jsonObject.getString("category_id");
			    }
			    data.item_url = jsonObject.getString("item_url");
			    flyerData.header_list.add(data);
			}
			
			JSONArray dataBodyArray = rootObject.getJSONArray("bodyFlyer");
			for (int i = 0; i < dataBodyArray.length(); i++) {
				JSONObject jsonObject = dataBodyArray.getJSONObject(i);

				FlyerBodyData data = flyerData.new FlyerBodyData();
			    data.img_url = jsonObject.getString("img_url");
			    data.item_url = jsonObject.getString("item_url");
			    flyerData.body_list.add(data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return flyerData;
	}

	@Override
	public void HttpClientProgress(float progress) {
		
	}
}
