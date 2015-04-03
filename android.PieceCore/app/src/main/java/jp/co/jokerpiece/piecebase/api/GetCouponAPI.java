package jp.co.jokerpiece.piecebase.api;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.util.HttpClient;
import jp.co.jokerpiece.piecebase.util.HttpClient.HttpClientInterface;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class GetCouponAPI extends AsyncTaskLoader<Boolean> implements HttpClientInterface {
	String couponID = null;
	public GetCouponAPI(Context context,String couponID) {
		super(context);
		this.couponID = couponID;
	}

	@Override
	public Boolean loadInBackground() {
		boolean stat = false;
		if(couponID == null){
			return stat;
		}
    	HashMap<String, String> parameter = new HashMap<String, String>();
    	parameter.put("app_id", Config.APP_ID);
    	parameter.put("uuid", Common.getUUID(getContext()));
    	parameter.put("coupon_id", couponID);
    	
        String result = null;
        try {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(Config.SENDID_GET_CPN, parameter,this);
            if(resData == null){
            	return stat;
            }
            result = new String(resData, "UTF-8");
            //Log.d("RESULT",result);        	
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return stat;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return stat;
		}
        try {
			JSONObject rootObject = new JSONObject(result);
			//Log.d("JSON", rootObject.toString());

			int error_code = rootObject.getInt("error_code");
			if(error_code == 0){
				stat = true;
			}else{
				return stat;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return stat;
	}

	@Override
	public void HttpClientProgress(float progress) {
		
	}
}
