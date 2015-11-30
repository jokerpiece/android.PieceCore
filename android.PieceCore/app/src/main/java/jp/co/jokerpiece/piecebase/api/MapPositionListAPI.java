package jp.co.jokerpiece.piecebase.api;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.ShopListData;
import jp.co.jokerpiece.piecebase.data.ShopListData.ShopData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.HttpClient;

/**
 * Created by kaku on 2015/04/14.
 */
public class MapPositionListAPI extends AsyncTaskLoader<ShopListData> implements HttpClient.HttpClientInterface {
    private String shopId;

    public MapPositionListAPI(Context context, String shopId) {
        super(context);
        this.shopId = shopId;
    }

    @Override
    public ShopListData loadInBackground() {
        ShopListData shopListData = new ShopListData();

        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("app_id", Config.APP_ID);
        parameter.put("app_key", Config.APP_KEY);
        String result = null;
        try {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(Config.SENDID_MAP_LIST , parameter,this);
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
            //AppUtil.debugLog("JSON", rootObject.toString());

            int error_code = rootObject.getInt("error_code");
            if(error_code != 0){
                return null;
            }

            shopListData.data_list = new ArrayList<ShopListData.ShopData>();
        if(!rootObject.isNull("more_flg")) {
            if (rootObject.getInt("more_flg") == 1) {
                shopListData.more_flg = true;
            } else {
                shopListData.more_flg = false;
            }
        }

            JSONArray dataArray = rootObject.getJSONArray("shopLists");
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject jsonObject = dataArray.getJSONObject(i);

                ShopData data = shopListData.new ShopData();
                data.shop_id = jsonObject.getString("shop_id");
                data.shop_name = jsonObject.getString("shop_name");
                data.longitude = jsonObject.getString("longitude");
                data.latitude = jsonObject.getString("latitude");
                data.address = jsonObject.getString("address");

                shopListData.data_list.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shopListData;
    }


    @Override
    public void HttpClientProgress(float progress) {

    }

}
