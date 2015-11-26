package jp.co.jokerpiece.piecebase.api;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.util.HttpClient;
import jp.co.jokerpiece.piecebase.util.HttpClient.HttpClientInterface;

public class GetLocationAPI extends AsyncTaskLoader<GetLocationAPI.LocationData> implements HttpClientInterface {
    String order_id = null;
    double latitude;
    double longitude;
    int type;

    public GetLocationAPI(Context context, String order_id, double lat, double lng, int type) {
        super(context);
        this.order_id = order_id;
        this.latitude = lat;
        this.longitude = lng;
        this.type = type;
    }

    @Override
    public LocationData loadInBackground() {
        LocationData data = null;
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("order_id", Config.APP_ID);
        parameter.put("uuid", Common.getUUID(getContext()));
        parameter.put("lat", String.valueOf(latitude));
        parameter.put("long", String.valueOf(longitude));
        parameter.put("type", String.valueOf(type));


        String result = null;
        try {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(Config.SENDID_GET_LOCATION, parameter,this);
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
                Log.d("error",rootObject.getString("error_message"));
                return null;
            }

            data = new LocationData();
            data.lat = rootObject.getDouble("lat");
            data.lng = rootObject.getDouble("long");
            data.updated = rootObject.getString("updated");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void HttpClientProgress(float progress) {

    }

    static public class LocationData{
        public double lat;
        public double lng;
        public String updated;
    }
}
