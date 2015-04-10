package jp.co.jokerpiece.piecebase.api;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.ItemListData;
import jp.co.jokerpiece.piecebase.data.ItemListData.ItemData;
import jp.co.jokerpiece.piecebase.util.HttpClient;
import jp.co.jokerpiece.piecebase.util.HttpClient.HttpClientInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class ItemListAPI extends AsyncTaskLoader<ItemListData> implements HttpClientInterface {
	int category_id = -1;
	String sarch_word = null;
	int page = -1;
	String coupon_id = null;
	public ItemListAPI(Context context,int category_id,String sarch_word,int page) {
		super(context);
		this.category_id = category_id;
		this.sarch_word = sarch_word;
		this.page = page;
	}

	public ItemListAPI(Context context,String coupon_id,int page) {
		super(context);
		this.coupon_id = coupon_id;
		this.page = page;
	}


	@Override
	public ItemListData loadInBackground() {
		ItemListData itemListData = new ItemListData();

        String result = null;
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("app_id", Config.APP_ID);
        String url = Config.SENDID_ITEM;
        if(category_id > 0){
        	parameter.put("category_id", String.valueOf(category_id));
        }
        if(sarch_word != null){
        	parameter.put("sarch_word", sarch_word);
        }
        if(coupon_id != null){
        	parameter.put("coupon_id", coupon_id);
        	url = Config.SENDID_ITEM_COUPON;
        }
    	parameter.put("get_list_num", String.valueOf(page));
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
			if(!rootObject.isNull("img_url")){
				itemListData.category_img_url = rootObject.getString("img_url");
			}
			itemListData.data_list = new ArrayList<ItemListData.ItemData>();
			itemListData.quantity  = rootObject.getString("quantity");
			if(rootObject.getInt("more_flg") == 1){
				itemListData.more_flg  = true;
			}else{
				itemListData.more_flg  = false;
			}

			JSONArray dataArray = rootObject.getJSONArray("itemLists");
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject jsonObject = dataArray.getJSONObject(i);

				ItemData data = itemListData.new ItemData();
				data.item_id = jsonObject.getString("item_id");
			    data.img_url = jsonObject.getString("img_url");
			    if(!jsonObject.isNull("item_name")){
			    	data.item_title = jsonObject.getString("item_name");
			    }
			    data.text = jsonObject.getString("text");
				data.price = jsonObject.getString("price");
			    data.item_url = jsonObject.getString("item_url");

			    itemListData.data_list.add(data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return itemListData;
	}

	@Override
	public void HttpClientProgress(float progress) {

	}
}
