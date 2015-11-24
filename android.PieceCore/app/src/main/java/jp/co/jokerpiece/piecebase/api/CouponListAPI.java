package jp.co.jokerpiece.piecebase.api;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.CouponListData;
import jp.co.jokerpiece.piecebase.data.CouponListData.CouponData;
import jp.co.jokerpiece.piecebase.util.HttpClient;
import jp.co.jokerpiece.piecebase.util.HttpClient.HttpClientInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

public class CouponListAPI extends AsyncTaskLoader<CouponListData> implements HttpClientInterface {
	int mode = -1;
	public CouponListAPI(Context context,int mode) {
		super(context);
		this.mode = mode;
	}

	@Override
	public CouponListData loadInBackground() {
		if(mode < 1){
			return null;
		}
		CouponListData couponData = new CouponListData();

    	HashMap<String, String> parameter = new HashMap<String, String>();
    	parameter.put("app_id", Config.APP_ID);
    	parameter.put("uuid", Common.getUUID(getContext()));
//		parameter.put("device_token",Common.getRegistrationId(getContext()));


        String result = null;
        String url = null;
        switch (mode) {
		case CouponListData.COUPON_DATA_TYPE_NOT_GIVE:
			url = Config.SENDID_CPN_GIVE;
			break;
		case CouponListData.COUPON_DATA_TYPE_GIVEN:
			url = Config.SENDID_CPN_TAKE;
			break;
		default:
			return null;
		}
        try {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(url, parameter,this);
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
			couponData.data_list = new ArrayList<CouponListData.CouponData>();

			JSONArray dataArray = rootObject.getJSONArray("couponLists");
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject jsonObject = dataArray.getJSONObject(i);

				CouponData data = couponData.new CouponData();

			    data.coupon_code = jsonObject.getString("coupon_code");

			    data.img_url = jsonObject.getString("img_url");
			    data.coupon_title = jsonObject.getString("title");
			    data.coupon_text = jsonObject.getString("text");
			    data.coupon_id = jsonObject.getString("coupon_id");
                data.coupon_url = jsonObject.getString("coupon_url");

			    couponData.data_list.add(data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return couponData;
	}

	@Override
	public void HttpClientProgress(float progress) {
		
	}
}
