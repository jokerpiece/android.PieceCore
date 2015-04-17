package jp.co.jokerpiece.piecebase.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.jokerpiece.piecebase.R;
import jp.co.jokerpiece.piecebase.data.MenuData;
import jp.co.jokerpiece.piecebase.util.App;

import android.content.Context;

public class Config {
    // --- staticイニシャライザで定数を設定(ここから) --- //
    public static final String APP_ID;
    public static final String APP_KEY;
    public static final int SPLASH_TIME;
//  public static String APP_ID = "pieceSample";
//  public static final String APP_ID = "pushColor";
//	public static final String APP_ID = "otonagokoro";
//	public static final String APP_ID = "kodomogokoro";
    // --- staticイニシャライザで定数を設定(ここまで) --- //

	//新大人ゴコロ本番
	public static final String SERVER_URL = "http://jokapi.jp/manager/html/xml/";

	//大人ゴコロ本番
	//public static final String SERVER_URL = "http://jokapi.jp/PieceProxy/service/api/shop/kodomogokoro/";
	//大人ゴコロステージング
	//public static final String SERVER_URL = "http://home.noumin.net/PieceProxy/service/api/shop/kodomogokoro/";
	//public static final String SERVER_URL = "http://192.168.77.68/piece/html/xml/";

	//http://192.168.77.68/piece/html/xml/device_token/index.php
	//スタブ
	//public static final String SERVER_URL = "http://192.168.77.83:8888/pieceSever/";

	//デバッグ用UUID デバッグではない場合nullに;
	//public static final String getDebugUUID = "jokerpiece-test-uuid";
	public static final String getDebugUUID = null;

    //OSタイプ (1:iOS, 2:android)
    public static final String OS_TYPE = "2";

	//サーバー接続までのタイムアウト時間
	public static final int connectionTimeOut = 5000;

	//サーバー接続後の読み込みタイムアウト時間
	public static final int readTimeOut = 30000;

	// プッシュ通知で必要なフィールド(ここから)
//	public static final String PROJECT_ID = "367759414941";	//プロジェクトIDを設定する
    public static final String PROJECT_ID;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // プッシュ通知で必要なフィールド(ここまで)
	//検索機能対応
	public static final boolean SEARCH_PERMISSION = true;
	public static final String USER_NAME = SERVER_URL + "home_list/";
	public static final String SENDID_NEWS_LIST = SERVER_URL + "news/";
	public static final String SENDID_NEWS_INFO = SERVER_URL + "news/index.php?Action=newsDetail";
	public static final String SENDID_FLIYER_LIST = SERVER_URL + "flyer/";
	public static final String SENDID_CTGRY = SERVER_URL + "category/";
	public static final String SENDID_ITEM = SERVER_URL + "item/index.php?Action=itemList";
	public static final String SENDID_ITEM_COUPON = SERVER_URL + "item/index.php?Action=itemList";
	public static final String SENDID_ITEM_BARCODE = SERVER_URL + "item/index.php?Action=itemListBarcode";
	public static final String SENDID_CPN_GIVE = SERVER_URL + "coupon/index.php?Action=giveList";
	public static final String SENDID_CPN_TAKE = SERVER_URL + "coupon/index.php?Action=takedList";
	public static final String SENDID_GET_CPN  = SERVER_URL + "coupon/index.php?Action=get";
    public static final String SENDID_GET_QUES  = SERVER_URL + "fitting/";
	public static final String PUSHSERVER_URL = SERVER_URL + "device_token/";
    public static final String SENDID_MAP_LIST = SERVER_URL + "shop/";

	public static final String PASS_WORD = null;

	public static final long scrollDelay = 4000;

	/** loaderCount */
	public static int loaderCnt = 0;
	/** タブ画面で戻るボタン押下時の動作 */
	public static final String BACK_STACK_KEY = "BACK_STACK";

	static public List<MenuData> getMenuList(Context context) {
		ArrayList<MenuData> dataList = new ArrayList<>();
		dataList.add(new MenuData(0,R.drawable.icon_flyer,context.getResources().getString(R.string.flyer0)));
		dataList.add(new MenuData(1,R.drawable.icon_infomation,context.getResources().getString(R.string.info)));
		dataList.add(new MenuData(2,R.drawable.icon_shopping,context.getResources().getString(R.string.shopping)));
		dataList.add(new MenuData(3,R.drawable.icon_coupon,context.getResources().getString(R.string.coupon)));
		return dataList;
	}

    static {
        boolean isSuccess = true;
        HashMap<String, String> map = new HashMap<>();
        try {
            ArrayList<String> lineList = new ArrayList<>();

            InputStream is = App.getContext().getResources().getAssets().open(
                    "settingFile.txt");
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String s;
            while ((s = br.readLine()) != null) {
                lineList.add(s);
            }
            br.close();
            isr.close();
            is.close();

            for (String line : lineList) {
                if (line.length() == 0) { continue; }
                String[] values = line.split("=");
                if (values[0] != null && !values[0].equals("") && values.length >= 2) {
                    map.put(values[0], values[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;

        }
        if (isSuccess) {
            String appId = map.get("app_id");
            if (appId != null && !appId.equals("")) {
                APP_ID = appId;
            } else {
                APP_ID = "pieceSample";
            }

            String appKey = map.get("app_key");
            if (appKey != null && !appKey.equals("")) {
                APP_KEY = appKey;
            } else {
                APP_KEY = "jokerpiece_appKey";
            }

            int splashTime;
            try {
                splashTime = Integer.valueOf(map.get("splash_time"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                splashTime = 2;
            }
            SPLASH_TIME = splashTime;

            String projectId = map.get("project_id");
            if (projectId != null && !projectId.equals("")) {
                PROJECT_ID = projectId;
            } else {
                PROJECT_ID = "367759414941";
            }
        } else {
            APP_ID = "pieceSample";
            APP_KEY = "jokerpiece_appKey";
            SPLASH_TIME = 2;
            PROJECT_ID = "367759414941";
        }
    }

}
